package com.example.bud

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.dataclasses.Nursery
import com.example.bud.utils.StorageImageLoader
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.content.edit

class ManagerSignUpActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var aboutInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var phonePrefixInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginLink: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_signup)

        val logoImage = findViewById<ImageView>(R.id.logoImage)
        StorageImageLoader.loadImage("design/bud_logo.png", logoImage, this)

        nameInput = findViewById(R.id.Nursery_name_Input)
        aboutInput = findViewById(R.id.about_nursery_input_signup)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        phonePrefixInput = findViewById(R.id.phonePrefixInput)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        addressInput = findViewById(R.id.address_input_signup)
        signUpButton = findViewById(R.id.signUpButton)
        loginLink = findViewById(R.id.goToLoginLink)

        loginLink.paintFlags = loginLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val about = aboutInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val phonePrefix = phonePrefixInput.text.toString().trim().ifEmpty { "+972" }
            val phone = phonePrefix + phoneNumberInput.text.toString().trim()
            val address = addressInput.text.toString().trim()

            if (email.isEmpty() || password.length < 6) {
                Toast.makeText(this, "נא להזין אימייל וסיסמה (לפחות 6 תווים)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // יצירת חשבון בפיירבייס אוטנטיקציה
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val nursery = Nursery(
                        nurseryName = name,
                        about = about,
                        email = email,
                        password = password,
                        phone = phone,
                        address = address,
                        createdAt = Timestamp.now()
                    )

                    db.collection("nurseries").add(nursery)
                        .addOnSuccessListener { docRef ->
                            getSharedPreferences("BUD_PREFS", MODE_PRIVATE).edit {
                                putString("NURSERY_ID", docRef.id)
                            }
                            getSharedPreferences("auth", MODE_PRIVATE).edit {
                                putString("role", "managers")
                                putString("email", email)
                                putString("phone", phone)
                            }

                            startActivity(Intent(this, ValidationActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "שמירת מנהל נכשלה: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "הרשמה נכשלה: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
