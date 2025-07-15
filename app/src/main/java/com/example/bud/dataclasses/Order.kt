package com.example.bud.dataclasses

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",               // מזהה ההזמנה (אוטומטי או UID)
    val customerId: String = "",            // מזהה הלקוח (email למשל)
    val nurseryName: String = "",           // המשתלה ממנה הוזמנו הצמחים
    val items: List<OrderItem> = listOf(),  // רשימת פריטים שהוזמנו
    val totalPrice: Double = 0.0,           // סה"כ לתשלום
    var status: String = "Pending",         // סטטוס (Pending, Paid, Cancelled)
    val createdAt: Timestamp = Timestamp.now() // תאריך יצירה
)

