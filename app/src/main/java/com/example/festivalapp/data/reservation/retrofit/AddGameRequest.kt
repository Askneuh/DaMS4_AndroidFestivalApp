package com.example.festivalapp.data.reservation.retrofit

import kotlinx.serialization.Serializable

@Serializable
data class AddGameRequest(
    val idGame: Int,
    val quantity: Int = 1
)