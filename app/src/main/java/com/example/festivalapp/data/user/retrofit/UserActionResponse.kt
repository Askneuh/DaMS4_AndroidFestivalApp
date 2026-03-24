package com.example.festivalapp.data.user.retrofit

import com.example.festivalapp.data.user.room.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class UserActionResponse(val message: String, val user: UserDto)