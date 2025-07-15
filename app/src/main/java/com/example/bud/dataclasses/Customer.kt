package com.example.bud.dataclasses

import com.google.firebase.Timestamp

data class Customer(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val birthDate: Timestamp? = null
)
