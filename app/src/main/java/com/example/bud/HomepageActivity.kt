package com.example.bud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.utils.StorageImageLoader

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        setupImages()
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            startActivity(Intent(this, CustomerSignUpActivity::class.java))
        }

        findViewById<Button>(R.id.nursery_signup_Button).setOnClickListener {
            startActivity(Intent(this, ManagerSignUpActivity::class.java))
        }
    }
    private fun setupImages() {

        StorageImageLoader.loadBackground("design/background3.png", findViewById(R.id.customerHomepageRoot), this)
        StorageImageLoader.loadImage("design/bud_logo.png", findViewById(R.id.logoImage), this)
    }

}
