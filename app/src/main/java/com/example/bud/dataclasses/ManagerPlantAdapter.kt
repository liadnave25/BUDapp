package com.example.bud.dataclasses

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bud.PlantDetailEditActivity
import com.example.bud.R

class ManagerPlantAdapter(private val plants: List<PlantWithId>) :
    RecyclerView.Adapter<ManagerPlantAdapter.PlantViewHolder>() {

    data class PlantWithId(val id: String, val plant: Plant)

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)
        val plantName: TextView = itemView.findViewById(R.id.plantName)
        val plantPrice: TextView = itemView.findViewById(R.id.plantPrice)
        val editButton: Button = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant_card, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plantWithId = plants[position]
        val plant = plantWithId.plant
        holder.plantName.text = plant.name
        holder.plantPrice.text = "${plant.price} ₪"
        Glide.with(holder.itemView.context).load(plant.imagePath).into(holder.plantImage)

        holder.editButton.visibility = View.VISIBLE
        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PlantDetailEditActivity::class.java)
            intent.putExtra("plantId", plantWithId.id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = plants.size
}
