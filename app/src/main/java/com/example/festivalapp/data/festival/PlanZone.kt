package com.example.festivalapp.data.festival

import kotlinx.serialization.Serializable

@Serializable
data class PlanZone(
    val id: Int = 0,
    val name: String = "",
    val nbTables: Int = 0,
    val festivalName: String = "",
    val tariffZoneId: Int = 0
)
