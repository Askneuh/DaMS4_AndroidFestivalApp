package com.example.festivalapp.data.user.retrofit

import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/me")
    suspend fun getMe(): Response<UserDto>

    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): Response<UserDto>

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<CreateUserRequest>

    @PUT("users/{userId}/role")
    suspend fun updateUserRole(
        @Path("userId") userId: Int,
        @Body request: UpdateRoleRequest
    ): Response<UserActionResponse>

    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): Response<UserActionResponse>
}
