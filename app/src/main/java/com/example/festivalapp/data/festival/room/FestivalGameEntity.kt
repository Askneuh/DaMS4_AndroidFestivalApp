package com.example.festivalapp.data.festival.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festival_games")
data class FestivalGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val festivalId: Int,
    val editorId: Int,
    val gameId: Int,
    val planZone: String = "",
    val allocatedTables: Int = 0
)
