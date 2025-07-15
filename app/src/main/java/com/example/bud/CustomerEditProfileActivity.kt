package com.example.bud
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.view.View
import com.example.bud.utils.StorageImageLoader

class CustomerEditProfileActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var phonePrefixInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var dateOfBirthInput: EditText
    private lateinit var saveButton: Button
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private var isPasswordVisible = false



    private val db = FirebaseFirestore.getInstance()
    private val customersCollection = db.collection("customers")
    private var customerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_edit_profile)
        val rootLayout = findViewById<View>(android.R.id.content).rootView
        StorageImageLoader.loadBackground("design/background1.png", rootLayout, this)

        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        StorageImageLoader.loadImage("design/eye_password_block.png", passwordToggle, this)
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        phonePrefixInput = findViewById(R.id.phonePrefixInput)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput)
        saveButton = findViewById(R.id.saveChangesButton)

        val sharedPref = getSharedPreferences("BUD_PREFS", MODE_PRIVATE)
        customerId = sharedPref.getString("CUSTOMER_ID", null)

        if (customerId == null) {
            Toast.makeText(this, "Customer ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // הצג את הסיסמה
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordInput.setSelection(passwordInput.text.length) // כדי שהסמן יישאר בסוף
                StorageImageLoader.loadImage("design/eye_password_seen.png", passwordToggle, this)
            } else {
                // הסתר את הסיסמה
                passwordInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordInput.setSelection(passwordInput.text.length)
                StorageImageLoader.loadImage("design/eye_password_block.png", passwordToggle, this)
            }
        }

        loadCustomerData()
        setupDatePicker()

        saveButton.setOnClickListener {
            saveCustomerData()
        }
    }

    private fun loadCustomerData() {
        customersCollection.document(customerId!!).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val email = doc.getString("email") ?: ""
                val password = doc.getString("password") ?: ""
                val phone = doc.getString("phone") ?: ""
                val birthDate = doc.getTimestamp("birthDate")
                val firstName = doc.getString("firstName") ?: ""
                val lastName = doc.getString("lastName") ?: ""

                firstNameInput.setText(firstName)
                lastNameInput.setText(lastName)
                emailInput.setText(email)
                passwordInput.setText(password)

                // ✨ הפרדת קידומת בינלאומית בצורה גמישה (ללא Regex)
                if (phone.startsWith("+")) {
                    var i = 1 // נתחיל אחרי ה־+
                    while (i < phone.length && phone[i].isDigit() && i <= 4) {
                        i++
                    }
                    val prefix = phone.substring(0, i) // למשל: +972, +1, +33
                    val number = phone.substring(i)
                    phonePrefixInput.setText(prefix)
                    phoneNumberInput.setText(number)
                } else {
                    // טלפון מקומי בלי קידומת
                    phonePrefixInput.setText("")
                    phoneNumberInput.setText(phone)
                }

                birthDate?.let {
                    val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.toDate())
                    dateOfBirthInput.setText(formatted)
                }
            }
        }
    }


    private fun saveCustomerData() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val phone = phonePrefixInput.text.toString().trim() + phoneNumberInput.text.toString().trim()
        val dateStr = dateOfBirthInput.text.toString().trim()


        val date = try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
        } catch (e: Exception) {
            Log.e("DateParseError", "Convert Date Failed", e)
            null
        }

        if (date == null) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "email" to email,
            "password" to password,
            "phone" to phone,
            "birthDate" to Timestamp(date)
        )

        customersCollection.document(customerId!!).update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    @SuppressLint("DefaultLocale")
    private fun setupDatePicker() {
        dateOfBirthInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateStr = dateOfBirthInput.text.toString()
            if (dateStr.isNotEmpty()) {
                try {
                    val parsed = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
                    calendar.time = parsed!!
                } catch (_: Exception) {}
            }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val formatted = String.format("%02d/%02d/%04d", d, m + 1, y)
                dateOfBirthInput.setText(formatted)
            }, year, month, day)

            datePicker.show()
        }
    }
}
