package com.example.bud

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bud.dataclasses.Plant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddPlantActivity : AppCompatActivity() {

    private lateinit var plantImage: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextName: EditText
    private lateinit var editTextQuantity: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextAbout: EditText
    private lateinit var saveButton: Button

    private var selectedImageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val categories = listOf(
        "Houseplants", "Outdoor plants", "Summer plants", "Winter plants", "Herbs"
    )

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(plantImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        val nurseryName = getSharedPreferences("BUD_PREFS", MODE_PRIVATE)
            .getString("nurseryName", "") ?: ""

        plantImage = findViewById(R.id.plantImage)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        editTextName = findViewById(R.id.editTextName)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextAbout = findViewById(R.id.editTextAbout)
        saveButton = findViewById(R.id.saveButton)

        setupCategorySpinner()
        plantImage.setOnClickListener { pickImageLauncher.launch("image/*") }

        saveButton.setOnClickListener {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = editTextName.text.toString().trim()
            val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
            val price = editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
            val about = editTextAbout.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter plant name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageAndSavePlant(selectedImageUri!!, name, quantity, price, about, category, nurseryName)
        }
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun uploadImageAndSavePlant(
        uri: Uri,
        name: String,
        quantity: Int,
        price: Double,
        about: String,
        category: String,
        nurseryName: String
    ) {
        val folder = when (category) {
            "Herbs" -> "plants/herbs_plants"
            "Houseplants" -> "plants/house_plants"
            "Outdoor plants" -> "plants/outdoor_plants"
            "Summer plants" -> "plants/summer_plants"
            "Winter plants" -> "plants/winter_plants"
            else -> "plants/uncategorized"
        }
        val fileName = "$folder/${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val ref = storage.reference.child(fileName)

        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                val plant = Plant(
                    name = name,
                    quantity = quantity,
                    price = price,
                    about = about,
                    imagePath = downloadUri.toString(),
                    nurseryName = nurseryName,
                    category = category
                )
                firestore.collection("plants")
                    .add(plant)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Plant added successfully", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)  //
                        finish()
                    }

                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save plant", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
}
