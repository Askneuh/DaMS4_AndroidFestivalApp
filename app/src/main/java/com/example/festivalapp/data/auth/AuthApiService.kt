package com.example.festivalapp.data.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    // POST /auth/login
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    // POST /auth/logout
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    // POST /auth/register
    @POST("auth/register")
    suspend fun register(@Body request: LoginRequest): Response<LoginResponse>

    // POST /auth/refresh
    @POST("auth/refresh")
    suspend fun refresh(): Response<Unit>

    // GET /auth/me
    @GET("auth/me")
    suspend fun getAuthenticatedUser(): Response<AuthUserResponse>

    // GET /auth/whoami
    @GET("auth/whoami")
    suspend fun whoami(): Response<AuthUserResponse>
}
