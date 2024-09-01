package com.example.mensageria.data.model

data class Mensage (
    val from : Phone
,   val to : Phone
,   val msg: String
,   val order: Long
    )