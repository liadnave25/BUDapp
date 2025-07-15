package com.example.bud

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bud.dataclasses.Plant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class PlantDetailEditActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var editTextQuantity: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextAbout: EditText
    private lateinit var saveButton: Button
    private var imageUri: Uri? = null
    private lateinit var plantId: String
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imageView.setImageURI(it)
        }
    }
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
    private lateinit var deleteButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail_edit)

        plantId = intent.getStringExtra("plantId") ?: return
        imageView = findViewById(R.id.plantImage)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextAbout = findViewById(R.id.editTextAbout)
        saveButton = findViewById(R.id.saveButton)
        imageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        loadPlantDetails()


        deleteButton = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener { deletePlant() }

        saveButton.setOnClickListener { savePlantDetails() }
    }

    private fun deletePlant() {
        val db = FirebaseFirestore.getInstance()
        val plantRef = db.collection("plants").document(plantId)

        plantRef.get().addOnSuccessListener { doc ->
            val imagePath = doc.getString("imagePath")

            // מחיקת מסמך מה-DB
            plantRef.delete().addOnSuccessListener {
                Toast.makeText(this, "הצמח נמחק", Toast.LENGTH_SHORT).show()
                // גם מוחק את התמונה מה-Storage אם יש
                imagePath?.let {
                    val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference(it)
                    storageRef.delete()
                }
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "מחיקה נכשלה", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "שגיאה בטעינת הצמח למחיקה", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }


    private fun loadPlantDetails() {
        FirebaseFirestore.getInstance().collection("plants").document(plantId)
            .get()
            .addOnSuccessListener { doc ->
                val plant = doc.toObject(Plant::class.java)
                if (plant != null) {
                    editTextQuantity.setText(plant.quantity.toString())
                    editTextPrice.setText(plant.price.toString())
                    editTextAbout.setText(plant.about)

                    Glide.with(this).load(plant.imagePath).into(imageView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load plant", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePlantDetails() {
        val newQuantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
        val newPrice = editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
        val newAbout = editTextAbout.text.toString()

        if (imageUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val ref = FirebaseStorage.getInstance().getReference("plants/$fileName")

            ref.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception!!
                    ref.downloadUrl
                }.addOnSuccessListener { uri ->
                    updatePlant(newQuantity, newPrice, newAbout, uri.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            updatePlant(newQuantity, newPrice, newAbout, null)
        }
    }

    private fun updatePlant(quantity: Int, price: Double, about: String, imagePath: String?) {
        val updates = mutableMapOf<String, Any>(
            "quantity" to quantity,
            "price" to price,
            "about" to about
        )
        if (imagePath != null) {
            updates["imagePath"] = imagePath
        }

        FirebaseFirestore.getInstance().collection("plants").document(plantId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Plant updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
            }
    }

}
