package com.example.festivalapp.data.festival

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TariffZoneDao {
    @Query("SELECT * FROM tariff_zone WHERE festivalName = :festivalName")
    fun getZonesByFestival(festivalName: String): Flow<List<TariffZoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: TariffZoneEntity)

    @Update
    suspend fun updateZone(zone: TariffZoneEntity)

    @Delete
    suspend fun deleteZone(zone: TariffZoneEntity)

    @Query("DELETE FROM tariff_zone WHERE festivalName = :festivalName")
    suspend fun deleteZonesByFestival(festivalName: String)

    @Query("DELETE FROM tariff_zone WHERE id = :zoneId")
    suspend fun deleteZoneById(zoneId: Int)

    @Query("SELECT * FROM tariff_zone WHERE idTZ = :idTZ LIMIT 1")
    fun getZoneById(idTZ: Int): Flow<TariffZoneEntity?>
}