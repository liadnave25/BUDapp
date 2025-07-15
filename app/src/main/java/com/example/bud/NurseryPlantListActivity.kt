package com.example.bud

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.dataclasses.Plant
import com.example.bud.dataclasses.ManagerPlantAdapter
import com.example.bud.dataclasses.ManagerPlantAdapter.PlantWithId
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class NurseryPlantListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var plantAdapter: ManagerPlantAdapter
    private val plantList = mutableListOf<PlantWithId>()

    private var category: String = ""
    private var nurseryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nursery_plant_list)

        recyclerView = findViewById(R.id.recyclerViewPlants)
        recyclerView.layoutManager = LinearLayoutManager(this)
        plantAdapter = ManagerPlantAdapter(plantList)
        recyclerView.adapter = plantAdapter

        // מקבל מה-intent
        category = intent.getStringExtra("category") ?: ""
        nurseryName = getSharedPreferences("BUD_PREFS", Context.MODE_PRIVATE)
            .getString("nurseryName", "") ?: ""

        loadPlants(category, nurseryName)

        val buttonAddPlant = findViewById<FloatingActionButton>(R.id.addPlantButton)
        buttonAddPlant.setOnClickListener {
            val intent = Intent(this, AddPlantActivity::class.java)
            startActivityForResult(intent, ADD_PLANT_REQUEST_CODE)
        }
    }

    private fun loadPlants(category: String, nurseryName: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("plants")
            .whereEqualTo("category", category)
            .whereEqualTo("nurseryName", nurseryName)
            .get()
            .addOnSuccessListener { result ->
                plantList.clear()
                for (doc in result) {
                    val plant = doc.toObject(Plant::class.java)
                    val id = doc.id
                    plantList.add(PlantWithId(id, plant))
                }
                plantAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load plants: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLANT_REQUEST_CODE && resultCode == RESULT_OK) {
            loadPlants(category, nurseryName)
        }
    }

    companion object {
        private const val ADD_PLANT_REQUEST_CODE = 1001
    }
}
