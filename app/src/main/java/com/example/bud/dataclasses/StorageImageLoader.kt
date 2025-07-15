package com.example.bud.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object StorageImageLoader {

    // טעינת תמונה ל־ImageView
    fun loadImage(path: String, into: ImageView, context: Context) {
        val ref = Firebase.storage.getReference(path)

        ref.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(context).load(uri).into(into)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Load Picture Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun loadBackground(path: String, into: View, context: Context) {
        val ref = Firebase.storage.getReference(path)

        ref.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            into.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
            .addOnFailureListener {
                Toast.makeText(context, "Load Background Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
