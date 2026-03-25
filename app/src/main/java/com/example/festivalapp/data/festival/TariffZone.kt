package com.example.festivalapp.data.festival

import kotlinx.serialization.Serializable

@Serializable
data class TariffZone(
    val idTZ: Int = 0,
    val name: String = "",
    val nbSmallTables: Int = 0,
    val nbLargeTables: Int = 0,
    val nbCityHallTables: Int = 0,
    val remainingSmallTables: Int = 0,
    val remainingLargeTables: Int = 0,
    val remainingCityHallTables: Int = 0,
    val smallTablePrice: Double = 0.0,
    val largeTablePrice: Double = 0.0,
    val cityHallTablePrice: Double = 0.0,
    val squareMeterPrice: Double = 0.0,
    val festivalName: String = ""
)