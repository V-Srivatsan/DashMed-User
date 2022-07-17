package com.dashmed.dashmed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.dashmed.R
import com.dashmed.dashmed.networking.Medicine

class SearchAdapter (private val context: Context, private val dataset: List<Medicine>, private val listener: SearchAdapter.OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.ItemViewHolder>() {

    inner class ItemViewHolder (private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemName: TextView = view.findViewById(R.id.package_search_name)
        val itemCost: TextView = view.findViewById(R.id.package_search_cost)
        var info: Medicine? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            info?.let { listener.itemListener(it) }
        }
    }

    interface OnItemClickListener {
        fun itemListener (info: Medicine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(context).inflate(R.layout.package_search_item, parent, false)
        return ItemViewHolder(adapter)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val med = dataset[position]

        holder.itemName.text = med.name
        val cost = med.cost.toString() + " INR"
        holder.itemCost.text = cost

        holder.info = med
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}