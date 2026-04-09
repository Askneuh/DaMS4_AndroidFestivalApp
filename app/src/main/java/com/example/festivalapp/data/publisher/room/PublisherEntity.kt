package com.example.festivalapp.data.publisher.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publishers")
data class PublisherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val festivalId: Int,
    val contactStatus: String = "NON_CONTACTE",
    val reservationStatus: String = "ABSENT",
    val lastContactDate: String? = null
)
