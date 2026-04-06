package com.example.festivalapp.data.reservation.retrofit

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGameRequest(
    val quantity: Int? = null,
    val isGamePlaced: Boolean? = null
)
