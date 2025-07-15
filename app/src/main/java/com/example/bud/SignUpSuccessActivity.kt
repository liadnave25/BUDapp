package com.example.bud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.utils.StorageImageLoader

class SignUpSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_success)
        loadSignupSuccessImages()
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadSignupSuccessImages() {
        StorageImageLoader.loadImage("design/bud_logo.png", findViewById(R.id.logoImage), this)
        StorageImageLoader.loadImage("design/success_login_women.png", findViewById(R.id.illustration), this)
    }


}

