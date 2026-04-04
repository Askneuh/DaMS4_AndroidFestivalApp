package com.example.festivalapp.data.user.room

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val login: String,
    val role: String
)