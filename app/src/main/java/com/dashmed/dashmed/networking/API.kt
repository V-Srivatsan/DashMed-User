package com.dashmed.dashmed.networking

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.http.*


private val BASEURL = "https://dashmed-av.herokuapp.com/users/api/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    ))
    .baseUrl(BASEURL)
    .build()


interface APIService {

    @POST("signup/")
    suspend fun signup(@Body data: SignupReq): AuthRes

    @POST("login/")
    suspend fun login(@Body data: LoginReq): AuthRes

    @POST("order/")
    suspend fun order(@Body data: OrderReq): EmptyRes


    @PUT("update/")
    suspend fun updateProfile(@Body data: UpdateReq): EmptyRes

    @DELETE("delete/")
    suspend fun deleteProfile(@Body data: DeleteReq): EmptyRes


    @GET("get-orders/")
    suspend fun getOrders(@Query("uid") uid: String): GetOrdersRes

    @GET("get-order/")
    suspend fun getOrder(@Query("uid") uid: String, @Query("order_id") order_id: String): GetOrderRes

    @GET("get-order-count/")
    suspend fun getOrdersCount(@Query("uid") uid: String): OrderCountRes

    @GET("get-medicines/")
    suspend fun getMedicines(@Query("query") query: String): MedicineQueryRes

    @GET("check-password/")
    suspend fun checkPassword(@Query("uid") uid: String, @Query("password") password: String): CheckPasswordRes

}

object API {
    val service : APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}