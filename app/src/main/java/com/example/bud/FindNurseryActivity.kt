package com.example.bud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.dataclasses.NurseryAdapter
import com.example.bud.dataclasses.Nursery
import com.google.firebase.firestore.FirebaseFirestore

class FindNurseryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NurseryAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_nursery)

        recyclerView = findViewById(R.id.nurseryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NurseryAdapter { selectedNursery ->
            val intent = Intent(this, CustomerViewNurseryPlantsActivity::class.java)
            intent.putExtra("nurseryName", selectedNursery.nurseryName)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        loadNurseries()
    }

    private fun loadNurseries() {
        db.collection("nurseries").get()
            .addOnSuccessListener { docs ->
                val list = docs.mapNotNull { it.toObject(Nursery::class.java) }
                adapter.updateData(list)
            }
            .addOnFailureListener { e ->
                Log.e("FindNursery", "Error loading nurseries", e)
            }
    }
}
