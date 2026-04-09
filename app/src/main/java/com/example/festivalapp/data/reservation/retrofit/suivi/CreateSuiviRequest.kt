package com.example.festivalapp.data.reservation.retrofit.suivi

import kotlinx.serialization.Serializable

@Serializable
data class CreateSuiviRequest(
    val status: String,
    val commentaire: String? = null,
    val idReservation: Int
)