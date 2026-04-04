package com.example.festivalapp.data.user.retrofit

@kotlinx.serialization.Serializable
data class CreateUserRequest(
    val login: String,
    val password: String,
    val role: String = "visiteur"
)