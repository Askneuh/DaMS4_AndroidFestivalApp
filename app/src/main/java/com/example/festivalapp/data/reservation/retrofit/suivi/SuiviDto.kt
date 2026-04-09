package com.example.festivalapp.data.reservation.retrofit.suivi

import com.example.festivalapp.data.reservation.room.SuiviReservation
import kotlinx.serialization.Serializable

@Serializable
data class SuiviReservationDto(
    val id: Int? = null,
    val status: String,
    val commentaire: String? = null,
    val date: String,
    val idReservation: Int
)

private fun SuiviReservationDto.toEntity() = SuiviReservation(
    id = id ?: 0,
    status = status,
    commentaire = commentaire,
    date = date,
    idReservation = idReservation
)