package com.example.festivalapp.data.festival

import kotlinx.serialization.Serializable

@Serializable
data class TariffZone(
    val idTZ: Int = 0,
    val name: String = "",
    val nbSmallTables: Int? = 0,
    val nbLargeTables: Int? = 0,
    val nbCityHallTables: Int? = 0,
    val remainingSmallTables: Int? = 0,
    val remainingLargeTables: Int? = 0,
    val remainingCityHallTables: Int? = 0,
    val smallTablePrice: Double? = null,
    val largeTablePrice: Double? = null,
    val cityHallTablePrice: Double? = null,
    val squareMeterPrice: Double? = null,
    val festivalName: String = ""
)