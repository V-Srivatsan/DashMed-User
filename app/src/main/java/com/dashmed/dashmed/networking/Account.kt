package com.dashmed.dashmed.networking

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class UpdateReq(
    @Json(name = "uid") var uid: String,
    @Json(name = "name") var name: String,
    @Json(name = "phone") var phone: String,
    @Json(name = "email") var email: String,
    @Json(name = "password") var password: String
)

@Keep
@JsonClass(generateAdapter = true)
data class DeleteReq (
    @Json(name = "uid") var uid: String
)

data class CheckPasswordRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "name") var name: String?,
    @Json(name = "email") var email: String?,
    @Json(name = "phone") var phone: String?
)