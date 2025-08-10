package com.example.bud
import androidx.core.content.edit
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class ValidationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var methodSelector: RadioGroup
    private lateinit var sendCodeButton: Button
    private lateinit var codeInput: EditText
    private lateinit var verifyButton: Button
    private lateinit var checkEmailVerifiedButton: Button
    private var verificationId: String? = null
    private lateinit var email: String
    private lateinit var phone: String
    private var emailVerificationSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.validation_activity)

        auth = FirebaseAuth.getInstance()
        checkEmailVerifiedButton = findViewById(R.id.checkEmailVerifiedButton)
        methodSelector = findViewById(R.id.methodSelector)
        sendCodeButton = findViewById(R.id.sendCodeButton)
        codeInput = findViewById(R.id.editTextCode)
        verifyButton = findViewById(R.id.verifyButton)

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        email = prefs.getString("email", "") ?: ""
        phone = prefs.getString("phone", "") ?: ""

        sendCodeButton.setOnClickListener {
            when (methodSelector.checkedRadioButtonId) {
                R.id.radioEmail -> sendEmailVerification()
                R.id.radioPhone -> startPhoneNumberVerification(phone)
            }
        }

        verifyButton.setOnClickListener {
            when (methodSelector.checkedRadioButtonId) {
                R.id.radioEmail -> {
                    auth.currentUser?.reload()?.addOnSuccessListener {
                        if (auth.currentUser?.isEmailVerified == true) {
                            Toast.makeText(this, "המייל אומת בהצלחה!", Toast.LENGTH_SHORT).show()
                            goToHomepage()
                        } else {
                            Toast.makeText(this, "המייל עדיין לא אומת. בדוק שוב.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                R.id.radioPhone -> {
                    val code = codeInput.text.toString()
                    verifyPhoneNumberWithCode(code)
                }
            }
        }

        checkEmailVerifiedButton.setOnClickListener {
            when (methodSelector.checkedRadioButtonId) {
                R.id.radioEmail -> {
                    auth.currentUser?.reload()?.addOnSuccessListener {
                        if (auth.currentUser?.isEmailVerified == true) {
                            goToHomepage()
                        } else {
                            Toast.makeText(this, "המייל עדיין לא אומת", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        if (user != null && !emailVerificationSent) {
            user.sendEmailVerification()
                .addOnSuccessListener {
                    emailVerificationSent = true
                    Toast.makeText(this, "קישור אימות נשלח למייל", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "שליחת המייל נכשלה: ${it.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "קישור כבר נשלח או שאין משתמש מחובר", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(phoneAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@ValidationActivity, "אימות נכשל: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            this@ValidationActivity.verificationId = verificationId
            Toast.makeText(this@ValidationActivity, "קוד נשלח בהצלחה", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        val id = verificationId
        if (id == null) {
            Toast.makeText(this, "שלח קוד קודם", Toast.LENGTH_SHORT).show()
            return
        }
        val credential = PhoneAuthProvider.getCredential(id, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(this, "אימות הצליח!", Toast.LENGTH_SHORT).show()
                goToHomepage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "שגיאה באימות: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun goToHomepage() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val role = prefs.getString("role", "")
        val email = prefs.getString("email", "")

        if (role == "managers") {
            val nurseryId = getSharedPreferences("BUD_PREFS", MODE_PRIVATE)
                .getString("NURSERY_ID", null)

            if (nurseryId != null) {
                FirebaseFirestore.getInstance().collection("nurseries")
                    .document(nurseryId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val nurseryName = doc.getString("nurseryName") ?: ""
                        getSharedPreferences("BUD_PREFS", MODE_PRIVATE).edit {
                            putString("nurseryName", nurseryName)
                        }
                        startActivity(Intent(this, ManagerHomepageActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "שגיאה בטעינת נתוני המשתלה", Toast.LENGTH_SHORT).show()
                    }
            }
        } else if (role == "customers") {
            // נשלוף את הלקוח לפי אימייל ונשמור את ה־ID
            FirebaseFirestore.getInstance().collection("customers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { docs ->
                    if (!docs.isEmpty) {
                        val doc = docs.first()
                        val customerId = doc.id
                        getSharedPreferences("BUD_PREFS", MODE_PRIVATE).edit {
                            putString("CUSTOMER_ID", customerId)
                        }
                        startActivity(Intent(this, CustomerHomepageActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "משתמש לא נמצא במערכת", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "שגיאה בגישה ללקוחות", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
