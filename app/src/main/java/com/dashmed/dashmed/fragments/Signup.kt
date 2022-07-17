package com.dashmed.dashmed.fragments

import android.content.Context
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
import com.dashmed.dashmed.databinding.FragmentSignupBinding
import com.dashmed.dashmed.viewmodels.SignupVM
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


class Signup : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignupVM
    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SignupVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignupBinding.inflate(inflater, container, false)
        coordinatorLayout = requireActivity().findViewById(R.id.login_coordinator_layout)

        viewModel.name = binding.signupName
        viewModel.phone = binding.signupPhone
        viewModel.email = binding.signupEmail
        viewModel.username = binding.signupUsername
        viewModel.password = binding.signupPassword

        binding.signupBtn.setOnClickListener {
            if (viewModel.validateFields()) {

                binding.signupBtn.visibility = View.GONE
                binding.signupProgress.visibility = View.VISIBLE

                viewModel.signupAccount().invokeOnCompletion {
                    val res = viewModel.res
                    if (! res.valid) {
                        res.message?.apply {
                            Utils.showSnackbar(coordinatorLayout, this)
                        }

                        binding.signupBtn.visibility = View.VISIBLE
                        binding.signupProgress.visibility = View.GONE

                    } else {
                        MainActivity.reloadFragment = true
                        with (requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit()) {
                            putString("UID", res.uid)
                            apply()
                        }
                        requireActivity().finish()
                    }
                }
            }
        }

        binding.loginLink.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                replace(R.id.login_container, Login())
            }.commit()
        }

        return binding.root
    }

}