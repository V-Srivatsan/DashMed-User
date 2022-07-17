package com.dashmed.dashmed.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.data.Item
import com.dashmed.dashmed.data.ItemDao
import com.dashmed.dashmed.data.PackageItem
import com.dashmed.dashmed.data.PackageItemDao
import com.dashmed.dashmed.networking.API
import com.dashmed.dashmed.networking.Medicine
import com.dashmed.dashmed.networking.MedicineQueryRes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchVM : ViewModel() {

    lateinit var itemDao: ItemDao
    lateinit var packageItemDao: PackageItemDao

    private var res: MedicineQueryRes? = null
    private var job: Job? = null

    fun getMedicines (query: String): Job? {
        job?.cancel()
        job = viewModelScope.launch {
            delay(300L)
            try {
                res = API.service.getMedicines(query)
            } catch (e: Exception) {
                res = null
            }
        }
        return job
    }

    fun addItemToPackage(info: Medicine, packageID: Int): Job {
        return viewModelScope.launch {
            itemDao.insert(Item(
                id = info.uid,
                name = info.name,
                description = info.description,
                composition = info.composition.joinToString(";"),
                expiration = info.expiration,
                cost = info.cost
            ))

            packageItemDao.insert(PackageItem(
                packageID = packageID,
                itemID = info.uid,
                quantity = 1
            ))
        }
    }

    fun getRes(): MedicineQueryRes? {
        return res
    }

}