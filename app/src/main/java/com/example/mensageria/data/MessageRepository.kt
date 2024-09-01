package com.example.mensageria.data

import android.util.Log
import com.example.mensageria.data.model.Contact
import com.example.mensageria.data.model.Mensage
import com.example.mensageria.data.model.Phone
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class MessageRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val messagesCollection = firestore.collection("Message")
    private val usersCollection = firestore.collection("User")

    suspend fun writeMessage(from: Phone, toStr: Phone, msg: String, o : Int): Boolean {
        return try {
            val messageData = hashMapOf(
                "from" to from.getPhone(),
                "to" to toStr.getPhone(),
                "message" to msg,
                "order" to o
            )
            messagesCollection.add(messageData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getMessagesByContactNumber(toString: String, fromString: String): List<Mensage> {
        return try {
            val query1: QuerySnapshot = messagesCollection
                .whereEqualTo("to", toString)
                .whereEqualTo("from", fromString)
                .get()
                .await()

            val query2: QuerySnapshot = messagesCollection
                .whereEqualTo("to", fromString)
                .whereEqualTo("from", toString)
                .get()
                .await()

            val allDocuments = mutableSetOf<DocumentSnapshot>()

            allDocuments.addAll(query1.documents)
            allDocuments.addAll(query2.documents)

            allDocuments.mapNotNull { document ->
                val fromPhoneStr = document.getString("from") ?: ""
                val toPhoneStr = document.getString("to") ?: ""
                val message = document.getString("message") ?: ""
                val order = document.getLong("order") ?: 0

                if (fromPhoneStr.isNotEmpty() && toPhoneStr.isNotEmpty() && message.isNotEmpty()) {
                    Mensage(
                        from = Phone(fromPhoneStr),
                        to = Phone(toPhoneStr),
                        msg = message,
                        order = order
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.i("getMessagesByContactNumber", "ERROR :" + e.printStackTrace().toString())
            emptyList()
        }
    }


    private suspend  fun listMenuContactNumberFromSavedContacts(contactNumbers: List<Contact>): List<Contact> {
        return try {
            val phoneNumbers = contactNumbers.map { it.phonenumber.getPhone() }

            if (phoneNumbers.isEmpty()) {
                return emptyList()
            }

            val querySnapshot: QuerySnapshot = usersCollection
                .whereIn("phoneNumber", phoneNumbers)
                .get()
                .await()

            val matchedContacts = contactNumbers.filter { contact ->
                querySnapshot.documents.any { document ->
                    document.getString("phoneNumber") == contact.phonenumber.getPhone()
                }
            }

            matchedContacts
        } catch (e: Exception) {
            Log.i("listMenuContactNumberFromSavedContacts", e.printStackTrace().toString())
            emptyList()
        }
    }

    suspend fun listMenuContactNumber(toPhone: Phone, contactNumbers: List<Contact>): List<Contact> {
        val fromMessages = listMenuContactNumberFromMessage(toPhone)
        Log.i("listMenuContactNumber",contactNumbers.toString())

        val fromSavedContacts = listMenuContactNumberFromSavedContacts(contactNumbers)
        Log.i("listMenuContactNumber",fromSavedContacts.toString())

        val contactMap = mutableMapOf<String, Contact>()

        for (contact in fromMessages) {
            contactMap[contact.phonenumber.getPhone()] = contact
        }
        Log.i("listMenuContactNumber",contactMap.toList().toString())

        for (contact in fromSavedContacts) {
            if(contactMap.containsKey(contact.phonenumber.getPhone())){
                contactMap.remove(contact.phonenumber.getPhone())
            }
            contactMap[contact.phonenumber.getPhone()] = contact
        }
        Log.i("listMenuContactNumber",contactMap.toList().toString())


        return contactMap.values.toList()
    }

    private suspend  fun listMenuContactNumberFromMessage(toPhone: Phone): List<Contact> {
        val querySnapshot: QuerySnapshot = messagesCollection
            .whereEqualTo("to", toPhone.getPhone())
            .get()
            .await()

        val distinctFromNumbers : List<String> = querySnapshot.documents
            .mapNotNull { it.getString("from") }
            .distinct()

        val contacts = mutableListOf<Contact>()
        for (fromNumber in distinctFromNumbers) {
            contacts.add(Contact(Phone(fromNumber), fromNumber))
        }

        return contacts
    }
}
