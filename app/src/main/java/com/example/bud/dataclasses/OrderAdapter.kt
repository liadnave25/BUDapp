package com.example.bud.dataclasses
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.R


class OrderAdapter(private val orderItems: List<OrderItem>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.textPlantName)
        val quantityText: TextView = itemView.findViewById(R.id.textQuantity)
        val unitPriceText: TextView = itemView.findViewById(R.id.textUnitPrice)
        val totalPriceText: TextView = itemView.findViewById(R.id.textTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = orderItems[position]
        holder.nameText.text = item.plantName
        holder.quantityText.text = "כמות: ${item.quantity}"
        holder.unitPriceText.text = "מחיר ליחידה: ${item.unitPrice}₪"
        holder.totalPriceText.text = "סה\"כ: ${"%.2f".format(item.totalPrice)}₪"
    }


    override fun getItemCount(): Int = orderItems.size
}