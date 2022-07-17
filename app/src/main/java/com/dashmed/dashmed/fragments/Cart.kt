package com.dashmed.dashmed.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.dashmed.CartActivity
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.PackageAdapter
import com.dashmed.dashmed.data.DB
import com.dashmed.dashmed.databinding.FragmentCartBinding
import com.dashmed.dashmed.viewmodels.CartVM
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class Cart : Fragment(), PackageAdapter.OnPackageClickListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: CartVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB.getDatabase(requireContext()).let {
            viewModel = ViewModelProvider(this)[CartVM::class.java]
            viewModel.packageDao = it.packageDao()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.cartPackageList.layoutManager = LinearLayoutManager(requireContext())
        lifecycle.coroutineScope.launch {
            viewModel.packageDao.getPackages().collect {
                if (it.count() == 0) {
                    binding.cartEmpty.visibility = View.VISIBLE
                } else {
                    binding.cartEmpty.visibility = View.GONE
                }
                binding.cartPackageList.adapter = PackageAdapter(requireContext(), it, this@Cart)
            }
        }

        binding.cartAddPackage.setOnClickListener {
            val dialog = Utils.showDialog(R.layout.package_dialog, inflater, requireContext())
            val input = dialog.findViewById<TextInputLayout>(R.id.package_prompt_name)
            val cancel = dialog.findViewById<Button>(R.id.package_prompt_cancel)
            val ok = dialog.findViewById<Button>(R.id.package_prompt_ok)

            cancel.setOnClickListener { dialog.dismiss() }
            ok.setOnClickListener {
                if (input.editText?.text.toString().isEmpty()) { input.error = "Package Name cannot be empty!" }
                else {
                    cancel.visibility = View.GONE
                    ok.visibility = View.GONE
                    dialog.findViewById<ProgressBar>(R.id.package_prompt_progress).visibility = View.VISIBLE
                    viewModel.addPackage(input.editText?.text.toString()).invokeOnCompletion { dialog.dismiss() }
                }
            }
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