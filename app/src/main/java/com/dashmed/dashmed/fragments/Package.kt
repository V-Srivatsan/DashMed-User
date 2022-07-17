package com.dashmed.dashmed.fragments

import Search
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.ItemsAdapter
import com.dashmed.dashmed.data.*
import com.dashmed.dashmed.data.Package
import com.dashmed.dashmed.databinding.FragmentPackageBinding
import com.dashmed.dashmed.viewmodels.PackageVM
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class Package : Fragment(), ItemsAdapter.OnItemClickListener, LocationListener {

    private lateinit var binding: FragmentPackageBinding
    private lateinit var viewModel: PackageVM
    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var db: DB
    private lateinit var packageItemDao: PackageItemDao
    private lateinit var itemDao: ItemDao

    private lateinit var locationManager: LocationManager
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.clearFragmentStack(requireActivity().supportFragmentManager)
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        db = DB.getDatabase(requireContext())
        packageItemDao = db.packageItemDao()
        itemDao = db.itemDao()

        viewModel = ViewModelProvider(this)[PackageVM::class.java]
        viewModel.context = requireContext()
    }

    override fun onLocationChanged(loc: Location) { location = loc }

    private fun askLocation() {
        if (location == null) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10f, this)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPackageBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.cart_coordinator_layout)

        askLocation()

        val packageId = requireActivity().intent.getIntExtra("package_id", -1)
        if (packageId != -1) {
            lifecycle.coroutineScope.launch {
                packageItemDao.getItemsOfPackage(packageId).collect {
                    if (it.count() == 0) {
                        binding.cartPackageEmpty.visibility = View.VISIBLE
                        binding.cartPackageItems.visibility = View.GONE
                        binding.cartPackageNetPrice.visibility = View.GONE
                        binding.cartPackageOrderBtn.isEnabled = false
                    } else {
                        binding.cartPackageEmpty.visibility = View.GONE
                        binding.cartPackageOrderBtn.isEnabled = (it.count() <= 20)

                        val dataset = mutableListOf<Pair<Item, PackageItem>>()
                        var price: Float = 0f

                        lifecycle.coroutineScope.launch {
                            for (item: Item in it) {
                                val packageItem = packageItemDao.getPackageItem(item.id, packageId)
                                price += (packageItem.quantity * item.cost)
                                dataset.add(item to packageItem)
                            }
                            binding.cartPackageItems.apply {
                                visibility = View.VISIBLE
                                adapter = ItemsAdapter(requireContext(), dataset, this@Package)
                                layoutManager = LinearLayoutManager(requireContext())
                            }
                            price = (price * 100).roundToInt() / 100f
                            binding.cartPackageNetPrice.text = "Total Price: ${price} INR"
                        }
                    }
                }
            }
        }

        binding.cartPackageAddItemBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                replace(R.id.cart_container, Search())
                addToBackStack(null)
            }.commit()
        }

        binding.cartPackageDeleteBtn.setOnClickListener {
            val dialog = Utils.showDialog(R.layout.package_delete_dialog, inflater, requireContext())
            dialog.findViewById<Button>(R.id.delete_package_prompt_cancel).setOnClickListener { dialog.dismiss() }
            dialog.findViewById<Button>(R.id.delete_package_prompt_ok).setOnClickListener {
                requireActivity().intent.getStringExtra("package_name")?.let {
                    lifecycle.coroutineScope.launch { db.packageDao().delete(Package(packageId, it)) }.invokeOnCompletion {
                        dialog.dismiss()
                        requireActivity().finish()
                    }
                }
            }
        }

        binding.cartPackageOrderBtn.setOnClickListener {
            val dialog = Utils.showDialog(R.layout.order_dialog, inflater, requireContext())
            lifecycle.coroutineScope.launch {
                if (location != null) {
                    requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("UID", null)?.let {
                        viewModel.placeOrder(packageItemDao.getPackageItems(packageId), it, location!!).invokeOnCompletion {
                            dialog.dismiss()
                            val res = viewModel.res
                            if (!res.valid) {res.message?.let { message -> Utils.showSnackbar(coordinatorLayout, message) }}
                            else { Utils.showSnackbar(coordinatorLayout, "Order placed successfully!") }
                        }
                    }
                }
                else {
                    dialog.dismiss()
                    Utils.showSnackbar(coordinatorLayout, "Please ensure that you have provided permission to access your location!")
                }
            }
        }

        return binding.root
    }

    override fun itemListener(info: Item, packageItem: PackageItem) {

        Utils.showBottomSheet(R.layout.item_package_modal, layoutInflater, requireContext()).apply {

            val saveBtn = findViewById<Button>(R.id.modal_item_save_btn)
            val quantityInput = findViewById<TextInputEditText>(R.id.modal_item_quantity)

            findViewById<TextView>(R.id.modal_item_name)?.text = info.name
            findViewById<TextView>(R.id.modal_item_cost)?.text = "${info.cost} INR"
            findViewById<TextView>(R.id.modal_item_description)?.text = info.description
            findViewById<TextView>(R.id.modal_item_expiration)?.text = "Expires in ${info.expiration} month(s)"
            quantityInput?.let {
                it.setText(packageItem.quantity.toString())
                it.addTextChangedListener {
                    saveBtn?.isEnabled = !it.isNullOrEmpty()
                }
            }

            findViewById<ChipGroup>(R.id.modal_item_composition)?.let {
                for (i: String in info.composition.split(";")) {
                    val chip = Chip(requireContext())
                    chip.text = i
                    it.addView(chip)
                }
            }

            saveBtn?.setOnClickListener {
                lifecycle.coroutineScope.launch {
                    packageItemDao.update(PackageItem(
                        id = packageItem.id,
                        packageID = packageItem.packageID,
                        itemID = packageItem.itemID,
                        quantity = quantityInput?.text.toString().toInt()
                    ))
                    this@apply.dismiss()
                }
            }

            findViewById<Button>(R.id.modal_item_remove_btn)?.setOnClickListener {
                val dialog = Utils.showDialog(R.layout.item_delete_dialog, layoutInflater, requireContext())
                dialog.findViewById<Button>(R.id.delete_item_prompt_cancel).setOnClickListener { dialog.dismiss() }
                dialog.findViewById<Button>(R.id.delete_item_prompt_ok).setOnClickListener {
                    dialog.dismiss()
                    this.dismiss()
                    lifecycle.coroutineScope.launch { packageItemDao.delete(packageItem) }
                }
            }
        }
    }
}