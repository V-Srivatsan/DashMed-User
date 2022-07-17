package com.dashmed.dashmed.viewmodels

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.R
import com.dashmed.dashmed.data.ItemDao
import com.dashmed.dashmed.data.PackageItem
import com.dashmed.dashmed.data.PackageItemDao
import com.dashmed.dashmed.networking.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class PackageVM : ViewModel() {

    lateinit var context: Context
    lateinit var res: EmptyRes

    fun placeOrder(data: List<PackageItem>, uid: String, location: Location): Job {
        val items = mutableListOf<ModelItem>()
        for (i: PackageItem in data) {
            items.add(ModelItem(i.itemID, i.quantity))
        }
        return viewModelScope.launch {
            try {
                res = API.service.order(OrderReq(
                    uid,
                    location.latitude,
                    location.longitude,
                    Geocoder(context).getFromLocation(location.latitude, location.longitude, 1)[0].getAddressLine(0),
                    items
                ))
            } catch (e: Exception) {
                res = EmptyRes(false, context.getString(R.string.network_error))
            }
        }
    }

}