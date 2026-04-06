package com.example.festivalapp.data.festival

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tariff_zone",
    foreignKeys = [
        ForeignKey(
            entity = FestivalEntity::class,
            parentColumns = ["name"],
            childColumns = ["festivalName"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TariffZoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idTZ: Int = 0,
    val name: String = "",
    val festivalName: String,
    val nbSmallTables: Int? = 0,
    val nbLargeTables: Int? = 0,
    val nbCityHallTables: Int? = 0,
    val smallTablePrice: Double? = null,
    val largeTablePrice: Double? = null,
    val cityHallTablePrice: Double? = null,
    val squareMeterPrice: Double? = null
)