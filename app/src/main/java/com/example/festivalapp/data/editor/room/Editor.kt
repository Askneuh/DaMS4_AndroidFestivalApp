package com.example.festivalapp.data.editor.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "editors")
data class Editor(
    @PrimaryKey val id: Int,
    val name: String,
    val exposant: Boolean,
    val distributeur: Boolean,
    val logo: String?
)

