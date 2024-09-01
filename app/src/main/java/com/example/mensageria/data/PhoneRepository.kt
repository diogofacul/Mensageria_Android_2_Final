package com.example.mensageria.data

import com.example.mensageria.data.model.Phone

class PhoneRepository {
    fun genPhoneByNumber(rawStrPhoneNumber : String) : Phone
    {
        return Phone(rawStrPhoneNumber)
    }
}