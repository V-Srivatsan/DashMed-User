package com.dashmed.dashmed.networking

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class SignupReq (
    @Json(name = "username") var username: String,
    @Json(name = "password") var password: String,
    @Json(name = "name") var name: String,
    @Json(name = "phone") var phone: String,
    @Json(name = "email") var email: String
)

@Keep
@JsonClass(generateAdapter = true)
data class LoginReq (
    @Json(name = "username") var username: String,
    @Json(name = "password") var password: String
)

data class AuthRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "UID") var uid: String?
)
