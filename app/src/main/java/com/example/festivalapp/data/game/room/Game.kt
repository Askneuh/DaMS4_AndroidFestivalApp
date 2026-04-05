package com.example.festivalapp.data.game.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: Int,
    val name: String,
    val author: String,
    val nbMinPlayer: Int,
    val nbMaxPlayer: Int,
    val minimumAge: Int,
    val duration: Int,
    val gameImage: String?,
    val idEditor: Int
)
