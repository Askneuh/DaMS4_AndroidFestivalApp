package com.example.festivalapp.data

import com.example.festivalapp.data.auth.LoginRequest
import com.example.festivalapp.data.auth.LoginResponse
import com.example.festivalapp.data.user.room.UserDto
import com.example.festivalapp.data.festival.Festival
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE

interface APIService {
    // ===== EXISTING ENDPOINTS =====
    @GET("users")
    suspend fun getUsers() : List<UserDto>

    @POST("auth/login")
    suspend fun loginUser(@Body lr: LoginRequest): Response<LoginResponse>

    // ===== NEW FESTIVAL ENDPOINTS =====
    @GET("festivals")
    suspend fun getAllFestivals(): List<Festival>

    @GET("festivals/{festivalName}")
    suspend fun getFestivalByName(@Path("festivalName") name: String): Festival

    @GET("festivals/current")
    suspend fun getCurrentFestival(): Festival

    @POST("festivals")
    suspend fun createFestival(@Body festival: Festival): Festival

    @POST("festivals/update/{festivalName}")
    suspend fun updateFestivalByName(
        @Path("festivalName") name: String,
        @Body festival: Festival
    ): Festival

    @POST("festivals/current/{festivalName}")
    suspend fun setCurrentFestival(@Path("festivalName") name: String): Festival

    @DELETE("festivals/{festivalName}")
    suspend fun deleteFestival(@Path("festivalName") name: String)
}