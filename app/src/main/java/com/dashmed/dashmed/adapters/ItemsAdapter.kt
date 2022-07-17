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

class ItemsAdapter (private val context: Context, private val dataset: List<Pair<Item, PackageItem>>, private val listener: ItemsAdapter.OnItemClickListener) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder (private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemName: TextView = view.findViewById(R.id.package_item_name)
        val itemCost: TextView = view.findViewById(R.id.package_item_cost)
        val itemQuantity: TextView = view.findViewById(R.id.package_item_quantity)
        var info: Item? = null
        var packageItem: PackageItem? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener.itemListener(info!!, packageItem!!)
        }
    }

    interface OnItemClickListener {
        fun itemListener (info: Item, packageItem: PackageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val med = dataset[position].first
        val packageItem = dataset[position].second

        holder.itemName.text = med.name
        holder.itemCost.text = "${med.cost} INR"
        holder.itemQuantity.text = "Quantity: ${packageItem.quantity}"

        holder.info = med
        holder.packageItem = packageItem
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}