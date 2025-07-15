package com.example.bud.dataclasses

import com.google.firebase.Timestamp

data class Nursery(
    val nurseryName: String = "",
    val about: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val address: String = "",
    val createdAt: Timestamp? = null
)
