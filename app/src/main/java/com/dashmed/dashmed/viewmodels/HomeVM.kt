package com.dashmed.dashmed.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.R
import com.dashmed.dashmed.networking.API
import com.dashmed.dashmed.networking.OrderCountRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeVM : ViewModel() {

    lateinit var orderRes: OrderCountRes

    fun getOrders(uid: String): Job {
        return viewModelScope.launch {
            try {
                orderRes = API.service.getOrdersCount(uid=uid)
            } catch (e: Exception) {
                orderRes = OrderCountRes(false, null, null)
            }
        }
    }
}