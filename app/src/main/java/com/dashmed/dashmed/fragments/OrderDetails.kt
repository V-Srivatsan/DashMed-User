package com.dashmed.dashmed.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.OrderItemsAdapter
import com.dashmed.dashmed.databinding.FragmentOrderDetailsBinding
import com.dashmed.dashmed.networking.OrderItem
import com.dashmed.dashmed.viewmodels.OrderVM
import kotlin.math.roundToInt


class OrderDetails : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var viewModel: OrderVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.clearFragmentStack(requireActivity().supportFragmentManager)

        viewModel = ViewModelProvider(this)[OrderVM::class.java]
        Utils.getUID(requireActivity())?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)

        var date = ""; var status = false

        requireActivity().intent.apply {
            getStringExtra("order_id")?.let { viewModel.orderId = it }
            getStringExtra("order_date")?.let { date = it }
            status = getBooleanExtra("order_status", false)
        }

        if (OrderVM.res == null)
            viewModel.getOrder().invokeOnCompletion {
                if (! OrderVM.res!!.valid)
                    requireActivity().finish()
                else
                    fillDetails(date, status)
            }
        else
            fillDetails(date, status)

        binding.orderItemsBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                replace(R.id.order_container, OrderItems())
                addToBackStack(null)
            }.commit()
        }

        return binding.root
    }

    private fun fillDetails(date: String, status: Boolean) {
        OrderVM.res?.let { res ->
            binding.orderEmployeeName.text = "Name: ${res.employee!!.name}"
            binding.orderEmployeeContact.text = "Contact: ${res.employee!!.phone}"
            binding.orderAddress.text = res.address

            binding.orderDate.text = date
            binding.orderStatus.apply {
                text = if (status) "Completed" else "Pending"
            }

            var total = 0f
            for (item in res.items!!) {
                total += (item.quantity * item.cost)
            }
            total = (total * 100).roundToInt() / 100f
            binding.orderCost.text = "$total INR"
        }
    }
}