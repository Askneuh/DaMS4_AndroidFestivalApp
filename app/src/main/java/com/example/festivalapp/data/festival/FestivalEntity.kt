package com.example.festivalapp.data.festival

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festival")
data class FestivalEntity(
    @PrimaryKey
    val name: String,
    val nbSmallTables: Int = 0,
    val nbLargeTables: Int = 0,
    val nbCityHallTables: Int = 0,
    val beginDate: String? = null,
    val endDate: String? = null,
    val isCurrent: Boolean = false,
    val creationDate: String? = null
)