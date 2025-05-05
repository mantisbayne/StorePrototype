package com.mantisbayne.storeprototype.data.api

import com.mantisbayne.storeprototype.data.CartDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CartApi {
    @GET("carts/{id}")
    suspend fun getCart(@Path("id") id: Int): CartDto
}