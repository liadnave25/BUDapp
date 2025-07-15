package com.example.bud
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.adapters.ExpandablePlantAdapter
import com.example.bud.dataclasses.Plant
import com.example.bud.utils.StorageImageLoader
import com.google.firebase.firestore.FirebaseFirestore

class FindPlantsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categorySpinner: Spinner
    private lateinit var adapter: ExpandablePlantAdapter
    private val plantList = mutableListOf<Plant>()
    private val allPlants = mutableListOf<Plant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_plants)
        val rootLayout = findViewById<View>(android.R.id.content).rootView
        StorageImageLoader.loadBackground("design/background4.png", rootLayout, this)

        recyclerView = findViewById(R.id.plantsRecyclerView)
        categorySpinner = findViewById(R.id.categorySpinner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExpandablePlantAdapter(plantList)
        recyclerView.adapter = adapter

        val categories = listOf("All", "Houseplants", "Outdoor plants", "Summer plants", "Winter plants", "Herbs")
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        val filterButton = findViewById<Button>(R.id.buttonFilter)
        filterButton.setOnClickListener {
            val selected = categorySpinner.selectedItem.toString()
            filterPlants(selected)
        }


        loadAllPlants()
    }

    private fun loadAllPlants() {
        FirebaseFirestore.getInstance().collection("plants").get()
            .addOnSuccessListener { result ->
                allPlants.clear()
                for (doc in result) {
                    val plant = doc.toObject(Plant::class.java)
                    allPlants.add(plant)
                }
                filterPlants("All")
            }
            .addOnFailureListener {
                Toast.makeText(this, "wrong upload plants", Toast.LENGTH_LONG).show()
            }
    }

    private fun filterPlants(category: String) {
        plantList.clear()
        plantList.addAll(if (category == "All") allPlants else allPlants.filter { it.category == category })
        adapter.notifyDataSetChanged()
    }
}
