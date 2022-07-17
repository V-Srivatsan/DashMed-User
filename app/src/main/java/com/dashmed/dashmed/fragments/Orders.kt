package com.dashmed.dashmed.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.dashmed.OrderActivity
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.OrdersAdapter
import com.dashmed.dashmed.databinding.FragmentOrdersBinding
import com.dashmed.dashmed.networking.Order
import com.dashmed.dashmed.networking.OrderItem
import com.dashmed.dashmed.viewmodels.OrdersVM
import kotlinx.coroutines.Job
import java.math.BigDecimal


class Orders : Fragment(), OrdersAdapter.OnOrderClickListener {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: OrdersVM

    private lateinit var orderJob: Job

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[OrdersVM::class.java]

        requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("UID", null)?.let {
            orderJob = viewModel.getOrders(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)

        orderJob.invokeOnCompletion { displayRes() }

        return binding.root
    }

    override fun orderListener(info: Order) {
        val intent = Intent(requireActivity(), OrderActivity::class.java)
        intent.putExtra("order_id", info.uid)
        intent.putExtra("order_date", info.date)
        intent.putExtra("order_status", info.status)
        startActivity(intent)
    }

    private fun displayRes() {
        val res = viewModel.res
        if (res.valid) {
            if (res.orders!!.isNotEmpty()) {
                binding.ordersList.layoutManager = LinearLayoutManager(requireContext())
                binding.ordersList.adapter = OrdersAdapter(requireContext(), res.orders ?: listOf(), this)
            } else
                binding.ordersEmpty.visibility = View.VISIBLE
        }
        else {
            binding.ordersEmpty.visibility = View.VISIBLE
            res.message?.let { message -> Utils.showSnackbar(coordinatorLayout, message) }
        }
        binding.ordersProgress.visibility = View.GONE
    }
}