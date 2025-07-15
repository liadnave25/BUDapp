package com.example.bud

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.dataclasses.Nursery
import com.example.bud.utils.StorageImageLoader
import com.google.firebase.firestore.FirebaseFirestore

class ManagerEditProfileActivity : AppCompatActivity() {

    private lateinit var nurseryNameInput: EditText
    private lateinit var aboutInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var saveButton: Button
    private var isPasswordVisible = false
    private val db = FirebaseFirestore.getInstance()
    private var nurseryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_edit_profile)
        loadManagerEditProfileImages()
        nurseryNameInput = findViewById(R.id.nurseryNameInput)
        aboutInput = findViewById(R.id.aboutInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        addressInput = findViewById(R.id.addressInput)
        saveButton = findViewById(R.id.saveChangesButton)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)


        val sharedPref = getSharedPreferences("BUD_PREFS", MODE_PRIVATE)
        nurseryId = sharedPref.getString("NURSERY_ID", null)

        if (nurseryId == null) {
            Toast.makeText(this, "Nursery ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadNurseryData()

        saveButton.setOnClickListener {
            saveNurseryData()
        }

        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordInput.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordInput.setSelection(passwordInput.text.length)
                StorageImageLoader.loadImage("design/eye_password_seen.png", passwordToggle, this)
            } else {
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordInput.setSelection(passwordInput.text.length)
                StorageImageLoader.loadImage("design/eye_password_block.png", passwordToggle, this)
            }
        }
    }

    private fun loadNurseryData() {
        db.collection("nurseries").document(nurseryId!!).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val nursery = doc.toObject(Nursery::class.java)
                    nursery?.let {
                        nurseryNameInput.setText(it.nurseryName)
                        aboutInput.setText(it.about)
                        emailInput.setText(it.email)
                        passwordInput.setText(it.password)
                        addressInput.setText(it.address)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load data: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveNurseryData() {
        val updated = mapOf(
            "about" to aboutInput.text.toString().trim(),
            "email" to emailInput.text.toString().trim(),
            "password" to passwordInput.text.toString().trim(),
            "address" to addressInput.text.toString().trim()
        )

        db.collection("nurseries").document(nurseryId!!).update(updated)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun loadManagerEditProfileImages() {
        StorageImageLoader.loadBackground("design/background1.png", findViewById(R.id.managerEditProfileRoot), this)
        StorageImageLoader.loadImage("design/bud_logo.png", findViewById(R.id.logoImage), this)
        StorageImageLoader.loadImage("design/eye_password_block.png", findViewById(R.id.passwordToggle), this)
    }


}
