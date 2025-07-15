package com.example.bud

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.utils.StorageImageLoader

class CustomerHomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_homepage)

        loadHomePageImages()

        val editProfile = findViewById<LinearLayout>(R.id.menuEditProfile)
        val logout = findViewById<LinearLayout>(R.id.menuLogout)
        val findNurseryCard = findViewById<LinearLayout>(R.id.menuFindNursery)
        val orderHistoryButton = findViewById<LinearLayout>(R.id.menuOrderHistory)

        // קבלת שם פרטי ומשפחה מה-SharedPreferences
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val firstName = prefs.getString("firstName", "") ?: ""
        val lastName = prefs.getString("lastName", "") ?: ""
        val fullName = "$firstName $lastName".trim()

        findViewById<TextView>(R.id.usernameText).text = if (fullName.isNotBlank()) fullName else "Customer"

        orderHistoryButton.setOnClickListener {
            val intent = Intent(this, CustomerOrdersActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.menuFindPlants).setOnClickListener {
            startActivity(Intent(this, FindPlantsActivity::class.java))
        }

        findNurseryCard.setOnClickListener {
            val intent = Intent(this, FindNurseryActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener {
            startActivity(Intent(this, CustomerEditProfileActivity::class.java))
        }

        logout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadHomePageImages() {
        StorageImageLoader.loadImage("design/edit_profile.png", findViewById(R.id.iconEditProfile), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowEditProfile), this)

        StorageImageLoader.loadImage("design/nursery_icon.png", findViewById(R.id.iconFindNursery), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowFindNursery), this)

        StorageImageLoader.loadImage("design/orders_icon.png", findViewById(R.id.iconOrderHistory), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowOrderHistory), this)

        StorageImageLoader.loadImage("design/logout.png", findViewById(R.id.iconLogout), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowLogout), this)

        StorageImageLoader.loadImage("design/plant_menu_right.png", findViewById(R.id.plantLeft), this)
        StorageImageLoader.loadImage("design/plant_menu_right.png", findViewById(R.id.plantRight), this)

        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowFindPlants), this)
        StorageImageLoader.loadImage("design/plant_menu.png", findViewById(R.id.iconFindPlants), this)
    }
}
