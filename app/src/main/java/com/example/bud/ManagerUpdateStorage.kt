package com.example.bud

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.utils.StorageImageLoader
import com.google.android.material.card.MaterialCardView

class ManagerUpdateStorage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manger_update_storage)


        loadCategoryImages()
        setupCategoryClicks()
    }

    // 🔄 רק טוען את התמונות לתוך ImageViews
    private fun loadCategoryImages() {
        StorageImageLoader.loadImage("design/houseplants_image.png", findViewById(R.id.houseplantsImage), this)
        StorageImageLoader.loadImage("design/outdoor_plants.png", findViewById(R.id.outdoorPlantsImage), this)
        StorageImageLoader.loadImage("design/summer_plants.png", findViewById(R.id.summerPlantsImage), this)
        StorageImageLoader.loadImage("design/winter_plants.png", findViewById(R.id.winterPlantsImage), this)
        StorageImageLoader.loadImage("design/herbs_plants.png", findViewById(R.id.herbsPlantsImage), this)
    }

    // 👆 הגדרת קליקים לכל קטגוריה
    private fun setupCategoryClicks() {
        findViewById<MaterialCardView>(R.id.card1).setOnClickListener {
            openPlantList("Houseplants")
        }

        findViewById<MaterialCardView>(R.id.card2).setOnClickListener {
            openPlantList("Outdoor plants")
        }

        findViewById<MaterialCardView>(R.id.card3).setOnClickListener {
            openPlantList("Summer plants")
        }

        findViewById<MaterialCardView>(R.id.card4).setOnClickListener {
            openPlantList("Winter plants")
        }

        findViewById<MaterialCardView>(R.id.card5).setOnClickListener {
            openPlantList("Herbs")
        }
    }

    private fun openPlantList(category: String) {
        val nurseryName = getSharedPreferences("BUD_PREFS", Context.MODE_PRIVATE)
            .getString("nurseryName", "") ?: ""

        val intent = Intent(this, NurseryPlantListActivity::class.java)
        intent.putExtra("category", category)
        intent.putExtra("nurseryName", nurseryName)
        startActivity(intent)
    }

}
