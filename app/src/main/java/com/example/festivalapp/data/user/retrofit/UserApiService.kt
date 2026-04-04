package com.example.festivalapp.data.user

import com.example.festivalapp.data.user.retrofit.CreateUserRequest
import com.example.festivalapp.data.user.retrofit.UpdateRoleRequest
import com.example.festivalapp.data.user.retrofit.UserActionResponse
import com.example.festivalapp.data.user.room.UserDto
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    // GET /users — admin uniquement
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    // GET /users/me — utilisateur connecté
    @GET("users/me")
    suspend fun getMe(): Response<UserDto>

    // GET /users/:userId
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): Response<UserDto>

    // POST /users — admin uniquement
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<CreateUserRequest>

    // PUT /users/:userId/role — admin uniquement
    @PUT("users/{userId}/role")
    suspend fun updateUserRole(
        @Path("userId") userId: Int,
        @Body request: UpdateRoleRequest
    ): Response<UserActionResponse>

    // DELETE /users/:userId — admin uniquement
    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): Response<UserActionResponse>
}
