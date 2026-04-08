package com.example.festivalapp.data.festival

import kotlinx.serialization.Serializable

@Serializable
data class Festival(
    val name: String = "",
    val nbSmallTables: Int = 0,
    val nbLargeTables: Int = 0,
    val nbCityHallTables: Int = 0,
    val remainingSmallTables: Int = 0,
    val remainingLargeTables: Int = 0,
    val remainingCityHallTables: Int = 0,
    val isCurrent: Boolean = false,
    val tariffZones: List<TariffZone> = emptyList(),
    val planZones: List<PlanZone> = emptyList(),
    val creationDate: String? = null,
    val beginDate: String? = null,
    val endDate: String? = null
)