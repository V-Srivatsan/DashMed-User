package com.dashmed.dashmed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.dashmed.R
import com.dashmed.dashmed.data.DB
import com.dashmed.dashmed.data.Item
import com.dashmed.dashmed.data.PackageItem
import com.dashmed.dashmed.networking.Medicine
import com.dashmed.dashmed.networking.OrderItem

class OrderItemsAdapter (private val context: Context, private val dataset: List<OrderItem>, private val listener: OrderItemsAdapter.OnItemClickListener) : RecyclerView.Adapter<OrderItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder (private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemName: TextView = view.findViewById(R.id.package_item_name)
        val itemCost: TextView = view.findViewById(R.id.package_item_cost)
        val itemQuantity: TextView = view.findViewById(R.id.package_item_quantity)

        lateinit var info: OrderItem

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener.itemListener(info)
        }
    }

    interface OnItemClickListener {
        fun itemListener (info: OrderItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.itemName.text = item.name
        holder.itemCost.text = "${item.cost} INR"
        holder.itemQuantity.text = "Quantity: ${item.quantity}"

        holder.info = item
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}