package com.example.festivalapp.data.user.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int = 0,
    val login: String,
    val password: String,
    val role: String
)