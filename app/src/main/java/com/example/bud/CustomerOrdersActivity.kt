package com.example.bud

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.dataclasses.Order
import com.example.bud.utils.StorageImageLoader
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class CustomerOrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_orders)

        val layout = findViewById<LinearLayout>(R.id.ordersContainer)
        StorageImageLoader.loadBackground("design/background_orders.jpg", layout, this)

        val customerId = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("email", null)

        if (customerId == null) {
            Toast.makeText(this, "שגיאה בזיהוי המשתמש", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("orders")
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "לא נמצאו הזמנות", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in documents) {
                    val order = doc.toObject(Order::class.java)

                    val card = MaterialCardView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 24)
                        }
                        radius = 16f
                        cardElevation = 4f
                        setCardBackgroundColor(getColor(R.color.white))
                        setPadding(32, 32, 32, 32)
                    }

                    val cardLayout = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                    }

                    val title = TextView(this).apply {
                        text = order.nurseryName
                        textSize = 18f
                        setTextColor(getColor(R.color.black))
                        setPadding(0, 0, 0, 8)
                    }

                    val total = TextView(this).apply {
                        text = "סה\"כ להזמנה: ₪${"%.2f".format(order.totalPrice)}"
                        textSize = 16f
                        setTextColor(getColor(R.color.black))
                    }

                    val status = TextView(this).apply {
                        text = order.status
                        textSize = 14f
                        setTextColor(getColor(R.color.white))
                        setPadding(16, 4, 16, 4)
                        gravity = Gravity.CENTER
                        setBackgroundResource(
                            when (order.status) {
                                "Pending" -> R.drawable.status_processing
                                "Paid" -> R.drawable.status_shipped
                                "Cancelled" -> R.drawable.status_delivered
                                else -> R.drawable.status_processing
                            }
                        )
                    }

                    val row = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.END
                        addView(total)
                        addView(status)
                    }

                    cardLayout.addView(title)
                    cardLayout.addView(row)

                    order.items.forEach { item ->
                        val itemText = TextView(this).apply {
                            text = "- ${item.plantName} (${item.quantity})"
                            textSize = 14f
                            setTextColor(getColor(R.color.black))
                        }
                        cardLayout.addView(itemText)
                    }

                    card.addView(cardLayout)
                    layout.addView(card)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "שגיאה בטעינת ההזמנות", Toast.LENGTH_SHORT).show()
            }
    }
}
