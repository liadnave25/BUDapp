package com.example.bud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.dataclasses.OrderAdapter
import com.example.bud.dataclasses.Order
import com.example.bud.dataclasses.OrderItem
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var payButton: Button
    private lateinit var continueShoppingButton: Button

    private lateinit var orderItemList: List<OrderItem>
    private var nurseryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        cartRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        totalTextView = findViewById(R.id.totalPriceText)
        payButton = findViewById(R.id.confirmOrderButton)
        continueShoppingButton = findViewById(R.id.backToShopButton)

        // שליפת נתונים
        orderItemList = CartManager.getItems()
        nurseryName = CartManager.getNurseryName()

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = OrderAdapter(orderItemList)

        updateTotal()

        continueShoppingButton.setOnClickListener {
            finish()
        }

        payButton.setOnClickListener {
            saveOrderToFirestore()
        }
    }

    private fun updateTotal() {
        val total = CartManager.getTotalPrice()
        totalTextView.text = "סה\"כ לתשלום: ₪$total"
    }

    private fun saveOrderToFirestore() {
        val db = FirebaseFirestore.getInstance()

        val customerId = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("email", "") ?: return

        val order = Order(
            customerId = customerId,
            nurseryName = nurseryName,
            items = orderItemList,
            totalPrice = CartManager.getTotalPrice(),
            status = "Pending"
        )

        db.collection("orders")
            .add(order)
            .addOnSuccessListener {
                CartManager.clearCart()
                Toast.makeText(this, "הזמנה נשלחה בהצלחה", Toast.LENGTH_SHORT).show()
                for (item in orderItemList) {
                    val plantQuery = db.collection("plants")
                        .whereEqualTo("nurseryName", nurseryName)
                        .whereEqualTo("name", item.plantName)
                        .limit(1)

                    plantQuery.get().addOnSuccessListener { docs ->
                        if (!docs.isEmpty) {
                            val doc = docs.documents[0]
                            val currentQty = doc.getLong("quantity") ?: 0
                            val newQty = currentQty - item.quantity

                            doc.reference.update("quantity", newQty.coerceAtLeast(0))  // שלא יירד מתחת ל־0
                        }
                    }
                }

                startActivity(Intent(this, PaymentSuccessActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "שגיאה בשמירת ההזמנה: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
