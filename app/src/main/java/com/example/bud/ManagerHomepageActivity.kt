package com.example.bud
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.R
import com.example.bud.utils.StorageImageLoader



class ManagerHomepageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_homepage)
        val prefs = getSharedPreferences("BUD_PREFS", MODE_PRIVATE)
        val nurseryName = prefs.getString("nurseryName", "Manager")
        findViewById<TextView>(R.id.usernameText).text = nurseryName


        loadManagerHomepageImages()
        setupClickListeners()

    }

    private fun loadManagerHomepageImages() {
        StorageImageLoader.loadImage("design/plant_menu_right.png", findViewById(R.id.plantLeft), this)
        StorageImageLoader.loadImage("design/plant_menu_right.png", findViewById(R.id.plantRight), this)

        StorageImageLoader.loadImage("design/edit_profile.png", findViewById(R.id.editIcon), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowEditProfileM), this)

        StorageImageLoader.loadImage("design/nursery_icon.png", findViewById(R.id.UpdateStorageIcon), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowUpdateStorage), this)

        StorageImageLoader.loadImage("design/orders_icon.png", findViewById(R.id.menuDeliveriesIcon), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.menuDeliveriesArrow), this)

        StorageImageLoader.loadImage("design/logout.png", findViewById(R.id.iconLogout), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowLogout), this)
        StorageImageLoader.loadImage("design/plant_menu.png", findViewById(R.id.iconFindPlants), this)
        StorageImageLoader.loadImage("design/arrow_right.png", findViewById(R.id.arrowFindPlants), this)

    }

    private fun setupClickListeners() {
        val editProfile = findViewById<View>(R.id.menuEditProfile)
        val updateStorage = findViewById<View>(R.id.menuUpdateStorage)
        val deliveries = findViewById<View>(R.id.menuDeliveries)
        val logout = findViewById<View>(R.id.menuLogout)
        val findPlants = findViewById<View>(R.id.menuFindPlants)
        findPlants.setOnClickListener {
            val intent = Intent(this, FindPlantsActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener {
            val intent = Intent(this,ManagerEditProfileActivity::class.java)
            startActivity(intent)
        }

        updateStorage.setOnClickListener {
            startActivity(Intent(this, ManagerUpdateStorage::class.java))
        }

        deliveries.setOnClickListener {
            val intent = Intent(this, NurseryOrdersActivity::class.java)
            startActivity(intent)
        }

        logout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}
