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
import com.dashmed.dashmed.networking.*
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AccountVM : ViewModel() {

    lateinit var uid: String
    lateinit var name: TextInputLayout
    lateinit var phone: TextInputLayout
    lateinit var email: TextInputLayout
    lateinit var password: TextInputLayout

    lateinit  var res: EmptyRes
    lateinit var checkRes: CheckPasswordRes

    private var isListening = false

    private fun addListeners() {
        if (! isListening) {
            isListening = true
            name.editText?.addTextChangedListener { validateFields() }
            phone.editText?.addTextChangedListener { validateFields() }
            email.editText?.addTextChangedListener { validateFields() }
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

        if (! Utils.validateField(password, Utils.Companion.VALIDATION_TYPE.PASSWORD))
            valid = false

        return valid
    }

    fun checkPassword(password: String): Job {
        return viewModelScope.launch {
            try {
                checkRes = API.service.checkPassword(uid, password)
            } catch (e: Exception) {
                checkRes = CheckPasswordRes(false, null, null, null, null)
            }
        }
    }

    fun updateAccount(): Job {
        return viewModelScope.launch {
            try {
                res = API.service.updateProfile(UpdateReq(
                    uid,
                    name.editText?.text.toString(),
                    phone.editText?.text.toString(),
                    email.editText?.text.toString(),
                    password.editText?.text.toString(),
                ))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

    fun deleteAccount(): Job {
        return viewModelScope.launch {
            try {
                res = API.service.deleteProfile(DeleteReq(uid))
            } catch (e: Exception) {
                res = EmptyRes(false, null)
            }
        }
    }

}