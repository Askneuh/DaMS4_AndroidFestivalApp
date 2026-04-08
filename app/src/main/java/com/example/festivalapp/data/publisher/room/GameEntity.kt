package com.example.festivalapp.data.publisher.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val authors: String = "",
    val publisherId: Int, // The global publisher
    val minAge: Int = 0,
    val maxAge: Int = 99
)
