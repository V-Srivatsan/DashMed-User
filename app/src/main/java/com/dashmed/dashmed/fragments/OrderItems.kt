package com.dashmed.dashmed.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.OrderItemsAdapter
import com.dashmed.dashmed.databinding.FragmentOrderItemsBinding
import com.dashmed.dashmed.networking.OrderItem
import com.dashmed.dashmed.viewmodels.OrderVM
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class OrderItems : Fragment(), OrderItemsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentOrderItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderItemsBinding.inflate(inflater, container, false)

        binding.orderItemsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = OrderItemsAdapter(requireContext(), OrderVM.res?.items ?: listOf(), this@OrderItems)
        }

        return binding.root
    }

    override fun itemListener(info: OrderItem) {
        Utils.showBottomSheet(R.layout.item_search_modal, layoutInflater, requireContext()).let {
            it.findViewById<Button>(R.id.modal_search_item_add_btn)?.visibility = View.GONE

            it.findViewById<TextView>(R.id.modal_search_item_name)?.text = info.name
            it.findViewById<TextView>(R.id.modal_search_item_cost)?.text = "${info.cost} INR"
            it.findViewById<TextView>(R.id.modal_search_item_description)?.text = info.description
            it.findViewById<TextView>(R.id.modal_search_item_expiration)?.text = "Expires in ${info.expiration} month(s)"
            it.findViewById<TextView>(R.id.modal_item_search_quantity)?.text = "Quantity: ${info.quantity}"

            it.findViewById<ChipGroup>(R.id.modal_search_item_composition)?.apply {
                for (item: String in info.composition) {
                    val chip = Chip(requireContext())
                    chip.text = item
                    addView(chip)
                }
            }
        }
    }
}