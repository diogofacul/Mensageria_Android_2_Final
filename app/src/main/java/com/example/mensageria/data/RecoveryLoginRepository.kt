package com.example.mensageria.data

import com.example.mensageria.data.model.RecoveryLoginCode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

class RecoveryLoginRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun genRecoveryLogin(rawPhoneStr: String, loginToken : String?): RecoveryLoginCode {
        return if(loginToken == null) {
            requestNewRegisterLogin(rawPhoneStr)
        } else{
            val rs = db.collection("LoginToken")
                .document(loginToken)
                .get().await()
            if(rs.exists()){
                return serializeRegisterLogin(rs)
            } else {
                return requestNewRegisterLogin(rawPhoneStr)
            }
        }

    }

    private fun requestNewRegisterLogin(rawPhoneStr : String) : RecoveryLoginCode {
        val rc = RecoveryLoginCode(PhoneRepository().genPhoneByNumber(rawPhoneStr))
        db.collection("User").document(rc.get_Phone().getPhone()).set(rc.toMap())
        db.collection("LoginToken").document(rc.get_uuid()).set(rc.toMap())
        return rc
    }

    private fun serializeRegisterLogin(rs: DocumentSnapshot): RecoveryLoginCode {
        val phoneNumber: String = rs.getString("phoneNumber") ?: ""
        val recoveryCode: Int = when (val code = rs.get("recoveryCode")) {
            is String -> code.toInt()
            is Number -> code.toInt()
            else -> 0
        }
        val uuid: String = rs.getString("uuid") ?: ""

        val rc = RecoveryLoginCode(PhoneRepository().genPhoneByNumber(phoneNumber))
        rc.set_uuid(uuid)
        rc.set_recovery_login_code(recoveryCode)
        return rc
    }
}
