package com.example.bud.dataclasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bud.R

class PlantAdapter(
    private val plantList: List<Plant>,
    private val onAddToCart: (OrderItem) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)
        val plantName: TextView = itemView.findViewById(R.id.plantName)
        val plantPrice: TextView = itemView.findViewById(R.id.plantPrice)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant_with_cart, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.plantName.text = plant.name
        holder.plantPrice.text = "${plant.price} ₪"
        Glide.with(holder.itemView.context).load(plant.imagePath).into(holder.plantImage)

        var quantity = 1
        holder.tvQuantity.text = quantity.toString()

        holder.btnIncrease.setOnClickListener {
            quantity++
            holder.tvQuantity.text = quantity.toString()
        }

        holder.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                holder.tvQuantity.text = quantity.toString()
            }
        }

        holder.btnAddToCart.setOnClickListener {
            val item = OrderItem(
                plantName = plant.name,
                quantity = quantity,
                unitPrice = plant.price
            )
            onAddToCart(item)
            Toast.makeText(holder.itemView.context, "נוסף לעגלה: ${plant.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = plantList.size
}
