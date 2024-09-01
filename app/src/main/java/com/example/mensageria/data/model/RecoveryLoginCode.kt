package com.example.mensageria.data.model

import java.util.UUID
import kotlin.random.Random

class RecoveryLoginCode(phone: Phone) {
    private var phoneNumber: Phone
    private var uuid: String
    private var recoveryCode: Int
    init{
        recoveryCode = generateRandomSixDigitNumber()
        uuid = UUID.randomUUID().toString()
        phoneNumber = phone
    }
    fun get_uuid(): String {
        return this.uuid
    }

    fun get_recovery_login_code(): Int {
        return this.recoveryCode
    }

    fun get_Phone(): Phone {
        return this.phoneNumber
    }

    fun set_recovery_login_code(code : Int){
        this.recoveryCode = code
    }

    fun set_uuid(uuid : String){
        this.uuid = uuid
    }

    private fun generateRandomSixDigitNumber(): Int {
        val lowerBound = 100000
        val upperBound = 999999

        return Random.nextInt(lowerBound, upperBound + 1)
    }

    fun toMap(): HashMap<String, Any?>
    {
        val map2 = HashMap<String, Any?>()
        map2.put("uuid",this.uuid)
        map2.put("recoveryCode",this.recoveryCode)
        map2.put("phoneNumber",phoneNumber.getPhone())

        return map2
    }
}
