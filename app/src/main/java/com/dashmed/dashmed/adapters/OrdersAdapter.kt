package com.dashmed.dashmed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.dashmed.R
import com.dashmed.dashmed.networking.Order

class OrdersAdapter (private val context: Context, private val dataset: List<Order>, private val listener: OnOrderClickListener) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder (private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val date: TextView = view.findViewById(R.id.order_item_date)
        val length: TextView = view.findViewById(R.id.order_item_length)
        val status: TextView = view.findViewById(R.id.order_item_status)

        lateinit var info: Order

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) { listener.orderListener(info) }
    }

    interface OnOrderClickListener {
        fun orderListener(info: Order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(layout)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = dataset[position]

        val d = order.date.split("-")
        holder.date.text = "Ordered on ${d[2]}-${d[1]}-${d[0]}"
        holder.length.text = "${order.length} item(s)"
        holder.status.text = if (order.status) "Completed" else "Pending"
        holder.info = order
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

}