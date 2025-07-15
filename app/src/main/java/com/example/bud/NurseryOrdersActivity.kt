package com.example.bud

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.dataclasses.Order
import com.google.firebase.firestore.FirebaseFirestore

class NurseryOrdersActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nurseryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nursery_deliveries)

        container = findViewById(R.id.ordersContainer)
        firestore = FirebaseFirestore.getInstance()

        // קבלת שם המשתלה מה־SharedPreferences
        nurseryName = getSharedPreferences("BUD_PREFS", MODE_PRIVATE)
            .getString("nurseryName", "")
            .toString()


        if (nurseryName.isEmpty()) {
            Toast.makeText(this, "לא נמצא שם משתלה", Toast.LENGTH_SHORT).show()
            return
        }

        loadOrdersForNursery()
    }

    private fun loadOrdersForNursery() {

        firestore.collection("orders")
            .whereEqualTo("nurseryName", nurseryName)
            .get()
            .addOnSuccessListener { result ->

                container.removeAllViews()

                for (doc in result) {

                    val order = doc.toObject(Order::class.java)
                    val docId = doc.id

                    val orderView = layoutInflater.inflate(R.layout.item_nursery_order_card, container, false)

                    val customerNameText = orderView.findViewById<TextView>(R.id.tvCustomerName)
                    val totalPriceText = orderView.findViewById<TextView>(R.id.tvTotalPrice)
                    val orderDateText = orderView.findViewById<TextView>(R.id.tvOrderDate)
                    val statusSpinner = orderView.findViewById<Spinner>(R.id.spinnerStatus)
                    val updateButton = orderView.findViewById<Button>(R.id.btnSaveStatus)

                    customerNameText.text = "לקוח: ${order.customerId}"
                    totalPriceText.text = "סה\"כ: ₪${order.totalPrice}"
                    orderDateText.text = "סטטוס נוכחי: ${order.status}"

                    // הגדרת Spinner
                    val options = listOf("Pending", "Paid", "Cancelled")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    statusSpinner.adapter = adapter
                    statusSpinner.setSelection(options.indexOf(order.status))

                    updateButton.setOnClickListener {
                        val newStatus = statusSpinner.selectedItem.toString()
                        firestore.collection("orders").document(docId)
                            .update("status", newStatus)
                            .addOnSuccessListener {
                                Toast.makeText(this, "עודכן ל-$newStatus", Toast.LENGTH_SHORT).show()
                                orderDateText.text = "סטטוס נוכחי: $newStatus"
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show()
                            }
                    }

                    container.addView(orderView)
                }

                if (result.isEmpty) {
                    val empty = TextView(this).apply {
                        text = "אין עדיין הזמנות למשתלה"
                        setTextColor(Color.DKGRAY)
                        textSize = 16f
                        setPadding(8, 8, 8, 8)
                    }
                    container.addView(empty)
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "שגיאה בטעינת הזמנות", Toast.LENGTH_LONG).show()
            }
    }
}
