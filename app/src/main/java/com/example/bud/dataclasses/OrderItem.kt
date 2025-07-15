package com.example.bud.dataclasses

data class OrderItem(
    val plantName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0
) {
    val totalPrice: Double get() = quantity * unitPrice
}

