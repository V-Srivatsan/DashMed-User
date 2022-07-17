package com.dashmed.dashmed.viewmodels

import android.content.Context
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.networking.API
import com.dashmed.dashmed.networking.AuthRes
import com.dashmed.dashmed.networking.LoginReq
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginVM : ViewModel() {

    lateinit var username: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit var res: AuthRes

    private var isListening = false

    private fun addListeners() {
        isListening = true
        username.editText?.addTextChangedListener { validateFields() }
        password.editText?.addTextChangedListener { validateFields() }
    }

    fun validateFields(): Boolean {
        if (! isListening) addListeners()
        var valid = true

        if (! Utils.validateField(username, Utils.Companion.VALIDATION_TYPE.TEXT, "Username"))
            valid = false

        if (! Utils.validateField(password, Utils.Companion.VALIDATION_TYPE.PASSWORD))
            valid = false

        return valid
    }

    fun login(): Job {
        return viewModelScope.launch {
            try {
                res = API.service.login(LoginReq(username.editText?.text.toString(), password.editText?.text.toString()))
            } catch (e: Exception) {
                res = AuthRes(false, null, null)
            }
        }
    }

}