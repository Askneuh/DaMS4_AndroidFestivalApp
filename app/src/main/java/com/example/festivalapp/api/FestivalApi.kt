package com.example.festivalapp.api

import com.example.festivalapp.data.example.Item
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET

interface FestivalApi {
    @POST("login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse
    
    @GET("items")
    suspend fun getItems(): List<Item>
}

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: User)
data class User(val id: Int, val name: String)
