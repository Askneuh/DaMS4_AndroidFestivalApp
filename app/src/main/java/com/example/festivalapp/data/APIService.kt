package com.example.festivalapp.data

import com.example.festivalapp.data.auth.LoginRequest
import com.example.festivalapp.data.auth.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("auth/login")
    suspend fun loginUser(@Body lr: LoginRequest): Response<LoginResponse>

}