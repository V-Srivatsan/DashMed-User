package com.dashmed.dashmed.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashmed.dashmed.data.PackageDao
import com.dashmed.dashmed.data.PackageItemDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CartVM : ViewModel() {

    lateinit var packageDao: PackageDao
    lateinit var packageItemDao: PackageItemDao

    fun addPackage(packageName: String): Job {
        return viewModelScope.launch {
            packageDao.insert(com.dashmed.dashmed.data.Package(name = packageName))
        }
    }

}