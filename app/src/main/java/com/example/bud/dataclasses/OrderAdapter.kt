package com.example.bud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bud.R
import com.example.bud.dataclasses.OrderItem
import androidx.appcompat.widget.AppCompatButton

class OrderAdapter(
    private val items: MutableList<OrderItem>,
    private val onIncrease: (position: Int) -> Unit,
    private val onDecrease: (position: Int) -> Unit,
    private val onRemove:   (position: Int) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderVH>() {

    inner class OrderVH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textPlantName)
        val unit: TextView = view.findViewById(R.id.textUnitPrice)
        val total: TextView = view.findViewById(R.id.textTotalPrice)

        val qty: TextView = view.findViewById(R.id.qtyValue)
        val btnInc: AppCompatButton = view.findViewById(R.id.btnIncrease)
        val btnDec: AppCompatButton = view.findViewById(R.id.btnDecrease)
        val btnRemove: AppCompatButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderVH(v)
    }

    override fun onBindViewHolder(h: OrderVH, position: Int) {
        val item = items[position]

        h.name.text = item.plantName

        h.unit.text  = "מחיר ליחידה: ${"%.2f".format(item.unitPrice)}₪"
        val displayTotal = item.unitPrice * item.quantity
        h.total.text = "סה\"כ: ${"%.2f".format(displayTotal)}₪"

        h.qty.text = item.quantity.toString()

        h.btnInc.setOnClickListener {
            val pos = h.adapterPosition
            if (pos != RecyclerView.NO_POSITION) onIncrease(pos)
        }
        h.btnDec.setOnClickListener {
            val pos = h.adapterPosition
            if (pos != RecyclerView.NO_POSITION) onDecrease(pos)
        }
        h.btnRemove.setOnClickListener {
            val pos = h.adapterPosition
            if (pos != RecyclerView.NO_POSITION) onRemove(pos)
        }

    }

    override fun getItemCount(): Int = items.size


    fun replaceAll(newItems: List<OrderItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
