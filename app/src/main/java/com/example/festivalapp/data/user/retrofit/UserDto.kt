package com.example.festivalapp.data.user.retrofit

import com.example.festivalapp.data.user.room.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val login: String,
    val role: String
)

fun UserDto.toRoomEntity(): User {
    return User(
        id = this.id,
        login = this.login,
        role = this.role
    )
}