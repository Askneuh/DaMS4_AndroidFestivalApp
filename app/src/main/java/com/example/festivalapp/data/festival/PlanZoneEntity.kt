package com.example.festivalapp.data.festival

import androidx.room.*

@Entity(
    tableName = "plan_zone",
    foreignKeys = [
        ForeignKey(
            entity = FestivalEntity::class,
            parentColumns = ["name"],
            childColumns = ["festivalName"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlanZoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val nbTables: Int,
    val festivalName: String,
    val tariffZoneId: Int // ID de la TariffZoneEntity liée
)
