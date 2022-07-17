package com.dashmed.dashmed.viewmodels

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.util.Patterns
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.R
import com.dashmed.dashmed.Utils
import com.dashmed.dashmed.networking.API
import com.dashmed.dashmed.networking.AuthRes
import com.dashmed.dashmed.networking.SignupReq
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SignupVM : ViewModel() {

    lateinit var name: TextInputLayout
    lateinit var phone: TextInputLayout
    lateinit var email: TextInputLayout
    lateinit var username: TextInputLayout
    lateinit var password: TextInputLayout
    lateinit  var res: AuthRes

    private var isListening = false

    private fun addListeners() {
        if (! isListening) {
            isListening = true
            name.editText?.addTextChangedListener { validateFields() }
            phone.editText?.addTextChangedListener { validateFields() }
            email.editText?.addTextChangedListener { validateFields() }
            username.editText?.addTextChangedListener { validateFields() }
            password.editText?.addTextChangedListener { validateFields() }
        }
    }

    fun validateFields(): Boolean {
        var valid = true
        addListeners()

        if (! Utils.validateField(name, Utils.Companion.VALIDATION_TYPE.TEXT))
            valid = false

        if (! Utils.validateField(phone, Utils.Companion.VALIDATION_TYPE.PHONE))
            valid = false

        if (! Utils.validateField(email, Utils.Companion.VALIDATION_TYPE.EMAIL))
            valid = false

        if (! Utils.validateField(username, Utils.Companion.VALIDATION_TYPE.TEXT))
            valid = false

        if (! Utils.validateField(password, Utils.Companion.VALIDATION_TYPE.PASSWORD))
            valid = false

        return valid
    }

    fun signupAccount(): Job {
        return viewModelScope.launch {
            try {
                res = API.service.signup(SignupReq(
                    username.editText?.text.toString(),
                    password.editText?.text.toString(),
                    name.editText?.text.toString(),
                    phone.editText?.text.toString(),
                    email.editText?.text.toString()
                ))
            } catch (e: Exception) {
                res = AuthRes(false, null, null)
            }
        }
    }

}