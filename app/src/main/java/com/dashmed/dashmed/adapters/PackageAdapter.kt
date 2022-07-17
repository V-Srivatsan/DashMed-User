package com.dashmed.dashmed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dashmed.dashmed.R
import com.dashmed.dashmed.data.Package


class PackageAdapter (private val context: Context, private val dataset: List<Package>, private val listener: OnPackageClickListener) : RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    inner class PackageViewHolder (private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val packageName: TextView = view.findViewById<TextView>(R.id.cart_package_name)
        var packageId = -1

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener.packageListener(packageName.text as String, packageId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.cart_package, parent, false)
        return PackageViewHolder(adapter)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val item = dataset[position]
        holder.packageName.text = item.name
        holder.packageId = item.id
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

    interface OnPackageClickListener {
        fun packageListener (name: String, packageId: Int)
    }

}