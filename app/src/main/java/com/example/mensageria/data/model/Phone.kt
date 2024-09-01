package com.example.mensageria.data.model

class Phone(rawPhoneStrInit : String) {
    private var rawphone : Long = 0

    init {
        setPhone(rawPhoneStrInit)
    }

    fun getPhone() : String {
        return formatPhoneNumber(this.rawphone)
    }

    private fun setPhone(rawphonestr : String){
        this.rawphone = removeNonNumeric(rawphonestr)
    }

    private fun formatPhoneNumber(number: Long): String {
        val numberStr = number.toString()

        if (numberStr.length > 14) {
            throw IllegalArgumentException("Número deve ser menor que 14 dígitos.")
        }

        return "+${numberStr.substring(0, 2)} ${numberStr.substring(2, 4)} ${numberStr.substring(4, 8)}-${numberStr.substring(8, numberStr.length)}"
    }

    private fun removeNonNumeric(input: String): Long {
        return input.replace(Regex("[^0-9]"), "").toLong()
    }
}