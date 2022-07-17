package com.dashmed.dashmed.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.R
import com.dashmed.dashmed.networking.API
import com.dashmed.dashmed.networking.GetOrdersRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class OrdersVM : ViewModel() {

    lateinit var res: GetOrdersRes

    fun getOrders(uid: String): Job {
        return viewModelScope.launch {
            try {
                res = API.service.getOrders(uid)
            } catch (e: Exception) {
                res = GetOrdersRes(false, null, listOf())
            }
        }
    }
}