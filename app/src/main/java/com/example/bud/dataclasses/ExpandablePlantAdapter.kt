package com.example.bud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bud.R
import com.example.bud.dataclasses.Plant

class ExpandablePlantAdapter(private val plants: List<Plant>) : RecyclerView.Adapter<ExpandablePlantAdapter.PlantViewHolder>() {

    private val expandedPositionSet = mutableSetOf<Int>()

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.plantImage)
        val name: TextView = itemView.findViewById(R.id.plantName)
        val readMore: TextView = itemView.findViewById(R.id.readMoreButton)
        val expandedLayout: View = itemView.findViewById(R.id.expandedLayout)
        val about: TextView = itemView.findViewById(R.id.plantAbout)
        val category: TextView = itemView.findViewById(R.id.plantCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expandable_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.name.text = plant.name
        Glide.with(holder.itemView.context).load(plant.imagePath).into(holder.image)

        val isExpanded = expandedPositionSet.contains(position)
        holder.expandedLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.readMore.text = if (isExpanded) "הסתר" else "קרא עוד"

        holder.readMore.setOnClickListener {
            if (isExpanded) {
                expandedPositionSet.remove(position)
            } else {
                expandedPositionSet.add(position)
            }
            notifyItemChanged(position)
        }

        holder.about.text = plant.about
        holder.category.text = "קטגוריה: ${plant.category}"
    }

    override fun getItemCount(): Int = plants.size
}
