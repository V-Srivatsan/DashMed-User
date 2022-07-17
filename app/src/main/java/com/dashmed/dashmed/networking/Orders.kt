package com.dashmed.dashmed.networking

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class OrderCountRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "counts") var counts: List<Int>?
)

data class GetOrdersRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "orders") var orders: List<Order>?
)

data class GetOrderRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "items") var items: List<OrderItem>?,
    @Json(name = "address") var address: String?,
    @Json(name = "lat") var lat: Double?,
    @Json(name = "long") var long: Double?,
    @Json(name = "employee") var employee: Employee?
)

data class MedicineQueryRes (
    @Json(name = "medicines") var medicines: List<Medicine>
)

@Keep
@JsonClass(generateAdapter = true)
data class OrderReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "lat") var lat: Double,
    @Json(name = "long") var long: Double,
    @Json(name = "address") var address: String,
    @Json(name = "meds") var meds: List<ModelItem>
)