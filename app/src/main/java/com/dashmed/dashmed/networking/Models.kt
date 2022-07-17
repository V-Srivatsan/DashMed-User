package com.dashmed.dashmed.networking

import com.squareup.moshi.Json

data class Medicine (
    @Json(name = "uid") var uid: String,
    @Json(name = "name") var name: String,
    @Json(name = "description") var description: String,
    @Json(name = "composition") var composition: List<String>,
    @Json(name = "expiration") var expiration: Int,
    @Json(name = "cost") var cost: Float
)


data class ModelItem (
    @Json(name = "uid") var uid: String,
    @Json(name = "quantity") var quantity: Int
)


data class Order (
    @Json(name = "uid") var uid: String,
    @Json(name = "date") var date: String,
    @Json(name = "status") var status: Boolean,
    @Json(name = "length") var length: Int,
)


data class OrderItem (
    @Json(name = "name") var name: String,
    @Json(name = "description") var description: String,
    @Json(name = "composition") var composition: List<String>,
    @Json(name = "expiration") var expiration: Int,
    @Json(name = "cost") var cost: Float,
    @Json(name = "quantity") var quantity: Int
)

data class Employee (
    @Json(name = "name") var name: String,
    @Json(name = "phone") var phone: String
)



data class EmptyRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?
)