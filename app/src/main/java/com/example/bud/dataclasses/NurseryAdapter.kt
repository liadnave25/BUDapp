package com.example.bud.dataclasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.R

class NurseryAdapter(
    private val onClick: (Nursery) -> Unit
) : RecyclerView.Adapter<NurseryAdapter.NurseryViewHolder>() {

    private val nurseryList = mutableListOf<Nursery>()

    inner class NurseryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nurseryName: TextView = itemView.findViewById(R.id.nurseryName)
        val nurseryAddress: TextView = itemView.findViewById(R.id.nurseryAddress)
        val nurseryPhone: TextView = itemView.findViewById(R.id.nurseryPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NurseryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nursery, parent, false)
        return NurseryViewHolder(view)
    }

    override fun onBindViewHolder(holder: NurseryViewHolder, position: Int) {
        val nursery = nurseryList[position]
        holder.nurseryName.text = nursery.nurseryName
        holder.nurseryAddress.text = nursery.address
        holder.nurseryPhone.text = "Phone: ${nursery.phone}"

        holder.itemView.setOnClickListener {
            onClick(nursery)
        }
    }

    override fun getItemCount(): Int = nurseryList.size

    fun updateData(newNurseries: List<Nursery>) {
        nurseryList.clear()
        nurseryList.addAll(newNurseries)
        notifyDataSetChanged()
    }
}
