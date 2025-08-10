package com.example.bud
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.dataclasses.CustomerPlantAdapter
import com.example.bud.dataclasses.Plant
import com.google.firebase.firestore.FirebaseFirestore

class CustomerViewNurseryPlantsActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var priceFilterEditText: EditText
    private lateinit var applyFilterButton: Button
    private lateinit var plantRecyclerView: RecyclerView
    private lateinit var checkoutButton: Button

    private lateinit var plantAdapter: CustomerPlantAdapter
    private val plantList = mutableListOf<Plant>()

    private val db = FirebaseFirestore.getInstance()
    private lateinit var nurseryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nursery_public_plant_list)

        categorySpinner = findViewById(R.id.categorySpinner)
        priceFilterEditText = findViewById(R.id.priceFilterEditText)
        applyFilterButton = findViewById(R.id.applyFilterButton)
        plantRecyclerView = findViewById(R.id.plantRecyclerView)
        checkoutButton = findViewById(R.id.checkoutButton)

        nurseryName = intent.getStringExtra("nurseryName") ?: ""

        val cleared = CartManager.setNurseryNameWithReset(nurseryName)
        if (cleared) {
            Toast.makeText(this, "You moved to another nursery — the cart has been reset.", Toast.LENGTH_SHORT).show()
        }

        setupSpinner()
        setupRecyclerView()
        loadPlants()

        applyFilterButton.setOnClickListener {
            loadPlants()
        }

        checkoutButton.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("nurseryName", nurseryName)
            startActivity(intent)
        }
    }

    private fun setupSpinner() {
        val categories = listOf("All", "Houseplants", "Outdoor plants", "Summer plants", "Winter plants", "Herbs")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun setupRecyclerView() {
        plantAdapter = CustomerPlantAdapter(plantList)
        plantRecyclerView.layoutManager = LinearLayoutManager(this)
        plantRecyclerView.adapter = plantAdapter
    }

    private fun loadPlants() {
        val selectedCategory = categorySpinner.selectedItem.toString()
        val maxPrice = priceFilterEditText.text.toString().toDoubleOrNull()

        var query = db.collection("plants").whereEqualTo("nurseryName", nurseryName)

        if (selectedCategory != "All") {
            query = query.whereEqualTo("category", selectedCategory)
        }

        query.get().addOnSuccessListener { docs ->
            plantList.clear()
            for (doc in docs) {
                val plant = doc.toObject(Plant::class.java)
                val priceOk = maxPrice == null || plant.price <= maxPrice
                if (priceOk) {
                    plantList.add(plant)
                }
            }
            plantAdapter.notifyDataSetChanged()
        }
    }
}
