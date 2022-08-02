package com.dashmed.dashmed.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.dashmed.dashmed.LoginActivity
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.data.DB
import com.dashmed.dashmed.databinding.FragmentAccountBinding
import com.dashmed.dashmed.viewmodels.AccountVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch


class Account : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountVM

    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AccountVM::class.java]
        Utils.getUID(requireActivity())?.let { viewModel.uid = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.main_coordinator_layout)
        val prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val dialog = Utils.showDialog(R.layout.password_dialog, inflater, requireContext())
        val input = dialog.findViewById<TextInputLayout>(R.id.password_prompt_password)
        val progress = dialog.findViewById<ProgressBar>(R.id.password_prompt_progress)
        val cancel = dialog.findViewById<Button>(R.id.password_prompt_cancel)
        val proceed = dialog.findViewById<Button>(R.id.password_prompt_ok)

        input.editText?.addTextChangedListener { input.isErrorEnabled = false }

        cancel.setOnClickListener {
            dialog.dismiss()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.home
        }

        proceed.setOnClickListener {
            if (Utils.validateField(input, Utils.Companion.VALIDATION_TYPE.PASSWORD)) {
                progress.visibility = View.VISIBLE
                proceed.visibility = View.GONE
                cancel.visibility = View.GONE

                viewModel.checkPassword(input.editText?.text.toString()).invokeOnCompletion {
                    val res = viewModel.checkRes
                    if (res.valid) {
                        dialog.dismiss()
                        binding.settingsName.text = res.name.toString()
                        binding.settingsEmail.text = res.email.toString()
                        binding.settingsPhone.text = res.phone.toString()
                        binding.settingsPassword.text = input.editText?.text.toString()
                    }
                    else {
                        input.error = res.message ?: getString(R.string.network_error)
                        proceed.visibility = View.VISIBLE
                        cancel.visibility = View.VISIBLE
                        progress.visibility = View.GONE
                    }
                }
            }
        }

        binding.editProfileBtn.setOnClickListener {
            Utils.showDialog(R.layout.profile_dialog, layoutInflater, requireContext()).apply {

                val progressBar: ProgressBar = findViewById(R.id.profile_prompt_progress)
                val cancelBtn: Button = findViewById(R.id.profile_prompt_cancel)
                val saveBtn: Button = findViewById(R.id.profile_prompt_ok)

                cancelBtn.setOnClickListener { this.dismiss() }

                viewModel.name = findViewById(R.id.profile_prompt_name)
                viewModel.email = findViewById(R.id.profile_prompt_email)
                viewModel.phone = findViewById(R.id.profile_prompt_phone)
                viewModel.password = findViewById(R.id.profile_prompt_password)

                viewModel.name.editText?.setText(binding.settingsName.text)
                viewModel.email.editText?.setText(binding.settingsEmail.text)
                viewModel.phone.editText?.setText(binding.settingsPhone.text)
                viewModel.password.editText?.setText(binding.settingsPassword.text)

                saveBtn.setOnClickListener {
                    if (viewModel.validateFields()) {
                        cancelBtn.visibility = View.GONE
                        saveBtn.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE

                        viewModel.updateAccount().invokeOnCompletion {
                            progressBar.visibility = View.GONE
                            saveBtn.visibility = View.VISIBLE
                            cancelBtn.visibility = View.VISIBLE
                            dismiss()

                            if (viewModel.res.valid)
                                Utils.showSnackbar(coordinatorLayout, "Profile updated successfully!")
                            else
                                Utils.showSnackbar(coordinatorLayout, viewModel.res.message.toString())
                        }
                    }
                }
            }
        }

        binding.signOutBtn.setOnClickListener {
            val db = DB.getDatabase(requireContext())
            lifecycle.coroutineScope.launch {
                db.packageDao().truncate()
                db.itemDao().truncate()
            }.invokeOnCompletion {
                with (prefs.edit()) {
                    clear()
                    apply()
                }
                requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }

        binding.deleteAccBtn.setOnClickListener {
            Utils.showDialog(R.layout.profile_delete_dialog, layoutInflater, requireContext()).apply {
                findViewById<Button>(R.id.profile_delete_dialog_cancel).setOnClickListener { this.dismiss() }
                findViewById<Button>(R.id.profile_delete_dialog_ok).setOnClickListener {
                    findViewById<LinearLayout>(R.id.profile_delete_dialog_message).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.profile_delete_dialog_progress).visibility = View.VISIBLE
                    viewModel.deleteAccount().invokeOnCompletion {
                        val res = viewModel.res
                        if (res.valid) {
                            DB.getDatabase(requireContext()).let {
                                lifecycle.coroutineScope.launch {
                                    it.packageDao().truncate()
                                    it.packageItemDao().truncate()
                                }.invokeOnCompletion {
                                    with (prefs.edit()) {
                                        clear()
                                        apply()
                                    }
                                    requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                }
                            }
                        } else {
                            dismiss()
                            Utils.showSnackbar(coordinatorLayout, res.message)
                        }
                    }
                }
            }
        }

        return binding.root
    }
}