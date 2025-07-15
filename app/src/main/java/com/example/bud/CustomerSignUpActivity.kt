package com.example.bud

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bud.dataclasses.Customer
import com.example.bud.utils.StorageImageLoader
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.edit

class CustomerSignUpActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_signup)

        StorageImageLoader.loadImage("design/bud_logo.png", findViewById(R.id.logoImage), this)

        val dateOfBirthInput = findViewById<EditText>(R.id.dateOfBirthInput)
        val loginLink = findViewById<TextView>(R.id.goToLoginLink)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        loginLink.paintFlags = loginLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        dateOfBirthInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val formatted = String.format("%02d/%02d/%04d", day, month + 1, year)
                    dateOfBirthInput.setText(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        signUpButton.setOnClickListener {
            val firstName = findViewById<EditText>(R.id.firstNameInput).text.toString().trim()
            val lastName = findViewById<EditText>(R.id.lastNameInput).text.toString().trim()
            val email = findViewById<EditText>(R.id.emailInput).text.toString().trim()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString().trim()
            val phonePrefix = findViewById<EditText>(R.id.phonePrefixInput).text.toString().trim().ifEmpty { "+972" }
            val phoneNumber = findViewById<EditText>(R.id.phoneNumberInput).text.toString().trim()
            val dateStr = dateOfBirthInput.text.toString().trim()

            if (email.isEmpty() || password.length < 6) {
                Toast.makeText(this, "יש להזין אימייל וסיסמה (לפחות 6 תווים)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = try {
                format.parse(dateStr)
            } catch (e: Exception) {
                null
            }

            if (parsedDate == null) {
                Toast.makeText(this, "תאריך לא תקין", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // הרשמה לפיירבייס אוטנטיקציה
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val customer = Customer(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        phone = "$phonePrefix$phoneNumber",
                        birthDate = Timestamp(parsedDate)
                    )

                    db.collection("customers")
                        .add(customer)
                        .addOnSuccessListener { doc ->
                            getSharedPreferences("BUD_PREFS", MODE_PRIVATE).edit {
                                putString("CUSTOMER_ID", doc.id)
                            }
                            getSharedPreferences("auth", MODE_PRIVATE).edit {
                                putString("role", "customers")
                                putString("email", email)
                                putString("phone", "$phonePrefix$phoneNumber")
                            }
                            startActivity(Intent(this, ValidationActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "שגיאה בשמירת לקוח: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "הרשמה נכשלה: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
