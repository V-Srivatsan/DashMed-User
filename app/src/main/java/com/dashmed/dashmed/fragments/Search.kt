
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.adapters.SearchAdapter
import com.dashmed.dashmed.data.DB
import com.dashmed.dashmed.databinding.FragmentSearchBinding
import com.dashmed.dashmed.networking.Medicine
import com.dashmed.dashmed.viewmodels.SearchVM
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch


class Search : Fragment(), SearchAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchVM
    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DB.getDatabase(requireContext()).let {
            viewModel = ViewModelProvider(this)[SearchVM::class.java]
            viewModel.itemDao = it.itemDao()
            viewModel.packageItemDao = it.packageItemDao()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.cart_coordinator_layout)

        binding.searchInput.addTextChangedListener {
            it?.apply {
                if (this.isEmpty() || this.length < 3) {
                    binding.searchEmpty.visibility = View.VISIBLE
                    binding.searchProgress.visibility = View.GONE
                    binding.searchList.visibility = View.GONE
                    binding.searchNone.visibility = View.GONE
                } else {
                    binding.searchEmpty.visibility = View.GONE
                    binding.searchNone.visibility = View.GONE
                    binding.searchProgress.visibility = View.VISIBLE

                    viewModel.getMedicines(this.toString())?.invokeOnCompletion {
                        val res = viewModel.getRes()
                        binding.searchProgress.visibility = View.GONE

                        if (res == null) { Utils.showSnackbar(coordinatorLayout, getString(R.string.network_error)) }
                        else if (res.medicines.isNotEmpty()) {
                            binding.searchNone.visibility = View.GONE
                            binding.searchList.apply {
                                visibility = View.VISIBLE
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = SearchAdapter(requireContext(), res.medicines, this@Search)
                            }
                        } else {
                            binding.searchList.visibility = View.GONE
                            binding.searchNone.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun itemListener(info: Medicine) {
        val packageId = requireActivity().intent.getIntExtra("package_id", -1)

        Utils.showBottomSheet(R.layout.item_search_modal, layoutInflater, requireContext()).apply {
            findViewById<TextView>(R.id.modal_search_item_name)?.text = info.name
            findViewById<TextView>(R.id.modal_search_item_cost)?.text = "${info.cost} INR"
            findViewById<TextView>(R.id.modal_search_item_description)?.text = info.description
            findViewById<TextView>(R.id.modal_search_item_expiration)?.text = "Expires in ${info.expiration} month(s)"

            findViewById<ChipGroup>(R.id.modal_search_item_composition)?.let {
                for (i: String in info.composition) {
                    val chip = Chip(requireContext())
                    chip.text = i
                    it.addView(chip)
                }
            }

            findViewById<Button>(R.id.modal_search_item_add_btn)?.let {

                lifecycle.coroutineScope.launch {
                    if (viewModel.packageItemDao.isItemInPackage(info.uid, packageId) > 0) {
                        it.isEnabled = false
                    }
                }

                it.setOnClickListener {
                    if (packageId != -1) {
                        lifecycle.coroutineScope.launch {
                            viewModel.addItemToPackage(info, packageId).invokeOnCompletion {
                                this@apply.dismiss()
                                Utils.showSnackbar(coordinatorLayout, "Item saved to this package!")
                            }
                        }
                    }
                }
            }
        }
    }
}