package com.example.mensageria.presenter

import DatastoreRepository
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mensageria.R
import com.example.mensageria.data.MessageRepository
import com.example.mensageria.data.model.Contact
import com.example.mensageria.data.model.Phone
import kotlinx.coroutines.launch

class ContactMenuActivity : AppCompatActivity() {
    private val requestContactPermission = 1001
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 10000
    private val updateRunnable = object : Runnable {
        override fun run() {
            loadContacts()
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_menu)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this@ContactMenuActivity)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                requestContactPermission)
        } else {
            loadContacts()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            val contacts = getContacts()
            val userPhone = Phone(DatastoreRepository.getPhone(this@ContactMenuActivity))
            val contactList = MessageRepository().listMenuContactNumber(userPhone, contacts)
            adapter = ContactAdapter(this@ContactMenuActivity, contactList) { contact ->
                val intent = Intent(this@ContactMenuActivity, ChatActivity::class.java)
                intent.putExtra("contact_number", contact.phonenumber.getPhone())
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestContactPermission && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts()
        }
    }

    private fun getContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val contentResolver = contentResolver

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            ),
            null,
            null,
            null
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            if (numberIndex != -1 && nameIndex != -1) {
                while (it.moveToNext()) {
                    val phoneNumber = it.getString(numberIndex)
                    val displayName = it.getString(nameIndex)
                    if (!phoneNumber.isNullOrEmpty() && !displayName.isNullOrEmpty()) {
                        contactList.add(Contact(Phone(phoneNumber), displayName))
                    }
                }
            } else {
                Log.e("ContactFetcher", "Column indexes for phone number or display name are invalid.")
            }
        }

        return contactList
    }
}
