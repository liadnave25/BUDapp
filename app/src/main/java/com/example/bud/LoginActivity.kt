package com.example.bud

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.utils.StorageImageLoader
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var chipCustomer: Chip
    private lateinit var chipNursery: Chip
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var logoImage: ImageView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        chipCustomer = findViewById(R.id.chipCustomer)
        chipNursery = findViewById(R.id.chipNursery)
        loginButton = findViewById(R.id.loginButton)
        signUpLink = findViewById(R.id.signUpLink)
        logoImage = findViewById(R.id.logoImage)

        db = FirebaseFirestore.getInstance()
        StorageImageLoader.loadImage("design/bud_logo.png", logoImage, this)

        signUpLink.paintFlags = signUpLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "יש להזין אימייל וסיסמה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val collection = when {
                chipCustomer.isChecked -> "customers"
                chipNursery.isChecked -> "nurseries"
                else -> {
                    Toast.makeText(this, "יש לבחור סוג משתמש (לקוח/מנהל)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            db.collection(collection)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { docs ->
                    if (docs.isEmpty) {
                        Toast.makeText(this, "האימייל לא נמצא במערכת", Toast.LENGTH_LONG).show()
                    } else {
                        val doc = docs.first()
                        val dbPassword = doc.getString("password") ?: ""
                        val emailFromDb = doc.getString("email") ?: ""
                        val phoneFromDb = doc.getString("phone") ?: ""

                        if (password == dbPassword) {
                            val authPrefs = getSharedPreferences("auth", MODE_PRIVATE)
                            authPrefs.edit {
                                putString("role", collection)
                                putString("email", emailFromDb)
                                putString("phone", phoneFromDb)
                            }

                            if (collection == "customers") {
                                val customerId = doc.id
                                val firstName = doc.getString("firstName") ?: ""
                                val lastName = doc.getString("lastName") ?: ""

                                getSharedPreferences("BUD_PREFS", MODE_PRIVATE).edit {
                                    putString("CUSTOMER_ID", customerId)
                                }

                                authPrefs.edit {
                                    putString("firstName", firstName)
                                    putString("lastName", lastName)
                                }

                                startActivity(Intent(this, CustomerHomepageActivity::class.java))

                            } else {
                                val nurseryId = doc.id
                                val nurseryName = doc.getString("nurseryName") ?: ""

                                getSharedPreferences("BUD_PREFS", MODE_PRIVATE).edit {
                                    putString("NURSERY_ID", nurseryId)
                                    putString("nurseryName", nurseryName)
                                }

                                startActivity(Intent(this, ManagerHomepageActivity::class.java))
                            }

                            finish()
                        } else {
                            Toast.makeText(this, "סיסמה שגויה", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "שגיאה בגישה למסד הנתונים", Toast.LENGTH_LONG).show()
                }
        }

        signUpLink.setOnClickListener {
            when {
                chipCustomer.isChecked -> {
                    startActivity(Intent(this, CustomerSignUpActivity::class.java))
                }
                chipNursery.isChecked -> {
                    startActivity(Intent(this, ManagerSignUpActivity::class.java))
                }
                else -> {
                    Toast.makeText(this, "בחר סוג משתמש", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
