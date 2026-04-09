package com.example.festivalapp.data.reservation.retrofit.reservation

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationRequest(
    val idEditor: Int,
    val festivalName: String,
    val status: String? = "Contact pris",
    val nbSmallTables: Int = 0,
    val nbLargeTables: Int = 0,
    val nbCityHallTables: Int = 0,
    val m2: Int = 0,
    val remise: Double = 0.0,
    val typeAnimateur: Int = 0,
    val listeDemandee: Boolean = false,
    val listeRecue: Boolean = false,
    val jeuxRecus: Boolean = false,
    val idTZ: Int? = null
)