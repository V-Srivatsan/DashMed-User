package com.dashmed.dashmed.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.dashmed.CartActivity
import com.dashmed.dashmed.MainActivity
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.PackageAdapter
import com.dashmed.dashmed.data.DB
import com.dashmed.dashmed.databinding.FragmentHomeBinding
import com.dashmed.dashmed.viewmodels.HomeVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Home : Fragment(), PackageAdapter.OnPackageClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeVM
    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeVM::class.java]
        Utils.clearFragmentStack(requireActivity().supportFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)

        lifecycle.coroutineScope.launch {
            DB.getDatabase(requireContext()).packageDao().getPackages().collect {
                if (it.isEmpty()) {
                    binding.homeCartPackages.visibility = View.GONE
                    binding.homeEmptyCart.visibility = View.VISIBLE
                } else {
                    binding.homeCartPackages.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = PackageAdapter(requireContext(), it.subList(0, Math.min(5, it.count())), this@Home)
                        visibility = View.VISIBLE
                    }
                    binding.homeEmptyCart.visibility = View.GONE
                }
            }
        }

        Utils.getUID(requireActivity())?.let {
            binding.homeGraphProgress.visibility = View.VISIBLE
            binding.homeEmptyOrders.visibility = View.GONE
            viewModel.getOrders(it).invokeOnCompletion {
                val res = viewModel.orderRes
                binding.homeGraphProgress.visibility = View.GONE
                if (! res.valid) {
                    Utils.showSnackbar(coordinatorLayout, res.message)
                    binding.homeEmptyOrders.visibility = View.VISIBLE
                } else {
                    res.counts?.apply {
                        binding.homeGraph.visibility = View.VISIBLE

                        val points = mutableListOf<DataPoint>()
                        for (i in this.indices) {
                            points.add(DataPoint((i + 1).toDouble(), this[i].toDouble()))
                        }
                        binding.homeGraph.addSeries(LineGraphSeries<DataPoint>(points.toTypedArray()))
                    }
                }
            }
        }

        binding.homeCartBtn.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.cart
        }

        return binding.root
    }

    override fun packageListener(name: String, packageId: Int) {
        val intent = Intent(requireActivity(), CartActivity::class.java)
        intent.putExtra("package_name", name)
        intent.putExtra("package_id", packageId)
        startActivity(intent)
    }
}