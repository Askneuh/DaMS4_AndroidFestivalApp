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
    val gameNotice: String,
    val idGameType: Int,
    val minimumAge: Int,
    val prototype: Boolean,
    val duration: Int,
    val theme: String,
    val description: String,
    val gameImage: String,
    val rulesTutorial: String,
    val edition: Int,
    val idEditor: Int
)
