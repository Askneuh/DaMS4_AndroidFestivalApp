package com.example.festivalapp.data.reservation.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey val idReservation: Int,
    val idEditor: Int,
    val festivalName: String,
    val status: String,
    val nbSmallTables: Int,
    val nbLargeTables: Int,
    val nbCityHallTables: Int,
    val m2: Int,
    val remise: Double,
    val idTZ: Int? // Peut être nul
)
