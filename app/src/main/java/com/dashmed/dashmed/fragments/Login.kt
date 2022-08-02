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
import androidx.lifecycle.ViewModelProvider
import com.dashmed.dashmed.MainActivity
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.databinding.FragmentLoginBinding
import com.dashmed.dashmed.viewmodels.LoginVM
import com.google.android.material.snackbar.Snackbar


class Login : Fragment() {

    private lateinit var viewModel: LoginVM
    private lateinit var binding: FragmentLoginBinding
    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().supportFragmentManager.popBackStack()
        viewModel = ViewModelProvider(this)[LoginVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.login_coordinator_layout)

        viewModel.username = binding.loginUsername
        viewModel.password = binding.loginPassword

        binding.loginBtn.setOnClickListener {
            if (viewModel.validateFields()) {
                binding.loginBtn.visibility = View.GONE
                binding.loginProgress.visibility = View.VISIBLE

                viewModel.login().invokeOnCompletion {
                    val res = viewModel.res
                    if (! res.valid) {
                        Utils.showSnackbar(coordinatorLayout, res.message)
                        binding.loginBtn.visibility = View.VISIBLE
                        binding.loginProgress.visibility = View.GONE
                    } else {
                        with (requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit()) {
                            putString("UID", res.uid)
                            apply()
                        }
                        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }

        binding.signupLink.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                replace(R.id.login_container, Signup())
                addToBackStack(null)
            }.commit()
        }

        return binding.root
    }
}