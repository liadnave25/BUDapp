package com.example.bud.dataclasses

data class Plant(
    val name: String = "",
    val category: String = "",
    val about: String = "",
    val imagePath: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val nurseryName: String = ""
)
