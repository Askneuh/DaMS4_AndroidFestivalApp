package com.example.festivalapp.data

import com.example.festivalapp.data.auth.LoginRequest
import com.example.festivalapp.data.auth.LoginResponse
import com.example.festivalapp.data.user.room.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIService {
    @GET("users")
    suspend fun getUsers() : List<UserDto>

    @POST("auth/login")
    suspend fun loginUser(@Body lr: LoginRequest): Response<LoginResponse>

}