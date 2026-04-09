package com.example.festivalapp.data.contact.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String?,
    val priority: Boolean,
    val idEditor: Int
)
