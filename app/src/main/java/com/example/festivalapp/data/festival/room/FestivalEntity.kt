package com.example.festivalapp.data.festival.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festivals")
data class FestivalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val totalTables: Int,
    val isCurrent: Boolean = false
)
