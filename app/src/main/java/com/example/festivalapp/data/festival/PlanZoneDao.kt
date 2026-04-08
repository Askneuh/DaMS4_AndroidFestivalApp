package com.example.festivalapp.data.festival

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanZoneDao {
    @Query("SELECT * FROM plan_zone WHERE festivalName = :festivalName")
    fun getPlanZonesByFestival(festivalName: String): Flow<List<PlanZoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanZone(planZone: PlanZoneEntity)

    @Update
    suspend fun updatePlanZone(planZone: PlanZoneEntity)

    @Delete
    suspend fun deletePlanZone(planZone: PlanZoneEntity)

    @Query("DELETE FROM plan_zone WHERE festivalName = :festivalName")
    suspend fun deletePlanZonesByFestival(festivalName: String)
}
