package com.dashmed.dashmed.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.networking.API
import com.dashmed.dashmed.networking.GetOrderRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderVM : ViewModel() {

    companion object {
        var res: GetOrderRes? = null
    }

    lateinit var uid: String
    lateinit var orderId: String

    fun getOrder(): Job {
        return viewModelScope.launch {
            try {
                res = API.service.getOrder(uid, orderId)
            } catch (e: Exception) {
                res = GetOrderRes(false, null, null, null, null, null, null)
            }
        }
    }

}