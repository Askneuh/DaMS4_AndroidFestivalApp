package com.example.festivalapp.data.reservation.retrofit

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReservationRequest(
    val status: String? = null,
    val nbSmallTables: Int? = null,
    val nbLargeTables: Int? = null,
    val nbCityHallTables: Int? = null,
    val m2: Int? = null,
    val remise: Double? = null,
    val typeAnimateur: Int? = null,
    val listeDemandee: Boolean? = null,
    val listeRecue: Boolean? = null,
    val jeuxRecus: Boolean? = null,
    val idTZ: Int? = null
)
