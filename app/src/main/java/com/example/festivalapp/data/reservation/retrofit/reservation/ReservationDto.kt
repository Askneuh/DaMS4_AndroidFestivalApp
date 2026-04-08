package com.example.festivalapp.data.reservation.retrofit.reservation

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDetailDto(
    val idReservation: Int,
    val idEditor: Int,
    val status: String,
    val nbSmallTables: Int,
    val nbLargeTables: Int,
    val nbCityHallTables: Int,
    val m2: Int,
    val remise: Double,
    val typeAnimateur: Int,
    val listeDemandee: Boolean,
    val listeRecue: Boolean,
    val jeuxRecus: Boolean,
    val festivalName: String,
    val idTZ: Int?,
    val editor: EditorInnerDto
)

@Serializable
data class EditorInnerDto(
    val id: Int,
    val name: String,
    val exposant: Boolean,
    val distributeur: Boolean,
    val logo: String?,
    val games: List<EditorGameDto> = emptyList()
)

@Serializable
data class EditorGameDto(
    val id: Int,
    val name: String,
    val author: String? = null,
    val gameImage: String? = null
)

@Serializable
data class ReservationGameDto(
    val id: Int,
    val name: String,
    val author: String? = null,
    val gameImage: String? = null,
    val isGamePlaced: Boolean,
    val quantity: Int,
    val idReservation: Int
)