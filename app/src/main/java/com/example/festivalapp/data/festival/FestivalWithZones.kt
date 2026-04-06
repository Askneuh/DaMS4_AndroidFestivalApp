package com.example.festivalapp.data.festival

import androidx.room.Embedded
import androidx.room.Relation

data class FestivalWithZones(
    @Embedded val festival: FestivalEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "festivalName"
    )
    val zones: List<TariffZoneEntity>
)
