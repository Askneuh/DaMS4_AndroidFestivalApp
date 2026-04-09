package com.example.festivalapp.data.reservation.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "suivi_reservation",
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["idReservation"],
            childColumns = ["idReservation"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idReservation"])]
)
data class SuiviReservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val status: String,
    val commentaire: String?,
    val date: String,
    val idReservation: Int
)
