package com.mantisbayne.storeprototype.data.api

import com.mantisbayne.storeprototype.data.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto
}