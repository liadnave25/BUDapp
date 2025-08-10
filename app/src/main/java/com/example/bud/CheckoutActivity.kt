package com.example.bud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.adapters.OrderAdapter
import com.example.bud.dataclasses.Order
import com.example.bud.dataclasses.OrderItem
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var payButton: Button
    private lateinit var continueShoppingButton: Button

    private lateinit var adapter: OrderAdapter
    private var nurseryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        cartRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        totalTextView = findViewById(R.id.totalPriceText)
        payButton = findViewById(R.id.confirmOrderButton)
        continueShoppingButton = findViewById(R.id.backToShopButton)

        nurseryName = CartManager.getNurseryName()

        adapter = OrderAdapter(
            items = CartManager.getItems().toMutableList(),
            onIncrease = { pos ->
                val current: OrderItem = CartManager.getItems()[pos]
                CartManager.updateQuantity(pos, current.quantity + 1)
                refreshCart()
            },
            onDecrease = { pos ->
                val current: OrderItem = CartManager.getItems()[pos]
                CartManager.updateQuantity(pos, (current.quantity - 1).coerceAtLeast(1))
                refreshCart()
            },
            onRemove = { pos ->
                CartManager.removeAt(pos)
                refreshCart()
            }
        )

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = adapter

        updateTotal()

        continueShoppingButton.setOnClickListener {
            if (nurseryName.isNotEmpty()) {
                val intent = Intent(this, CustomerViewNurseryPlantsActivity::class.java)
                intent.putExtra("nurseryName", nurseryName)
                startActivity(intent)
            } else {
                finish()
            }
        }

        payButton.setOnClickListener {
            saveOrderToFirestore()
        }
    }

    private fun refreshCart() {
        adapter.replaceAll(CartManager.getItems())
        updateTotal()
    }

    private fun updateTotal() {
        val total = CartManager.getTotalPrice()
        totalTextView.text = "₪${"%.2f".format(total)}"
    }

    private fun saveOrderToFirestore() {
        val total = CartManager.getTotalPrice()
        if (total <= 0) {
            Toast.makeText(this, "You can't place an order with 0 shekels", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val customerId = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("email", "") ?: return

        val itemsSnapshot: List<OrderItem> = CartManager.getItems()

        val order = Order(
            customerId = customerId,
            nurseryName = nurseryName,
            items = itemsSnapshot,
            totalPrice = total,
            status = "Pending"
        )

        db.collection("orders")
            .add(order)
            .addOnSuccessListener {
                for (item in itemsSnapshot) {
                    val plantQuery = db.collection("plants")
                        .whereEqualTo("nurseryName", nurseryName)
                        .whereEqualTo("name", item.plantName)
                        .limit(1)

                    plantQuery.get().addOnSuccessListener { docs ->
                        if (!docs.isEmpty) {
                            val doc = docs.documents[0]
                            val currentQty = doc.getLong("quantity") ?: 0
                            val newQty = currentQty - item.quantity
                            doc.reference.update("quantity", newQty.coerceAtLeast(0))
                        }
                    }
                }

                CartManager.clearCart()
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, PaymentSuccessActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
