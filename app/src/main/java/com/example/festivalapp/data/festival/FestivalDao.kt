package com.example.festivalapp.data.festival

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    @Query("SELECT * FROM festival")
    fun getAllFestivals(): Flow<List<FestivalEntity>>

    @Transaction
    @Query("SELECT * FROM festival")
    fun getAllFestivalsWithZones(): Flow<List<FestivalWithZones>>

    @Query("SELECT * FROM festival WHERE name = :name")
    fun getFestivalByName(name: String): Flow<FestivalEntity?>

    @Transaction
    @Query("SELECT * FROM festival WHERE name = :name")
    fun getFestivalWithZonesByName(name: String): Flow<FestivalWithZones?>

    @Query("SELECT * FROM festival WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentFestival(): Flow<FestivalEntity?>

    @Transaction
    @Query("SELECT * FROM festival WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentFestivalWithZones(): Flow<FestivalWithZones?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestival(festival: FestivalEntity)

    @Update
    suspend fun updateFestival(festival: FestivalEntity)

    @Delete
    suspend fun deleteFestival(festival: FestivalEntity)

    @Query("DELETE FROM festival WHERE name = :name")
    suspend fun deleteFestivalByName(name: String)
}
