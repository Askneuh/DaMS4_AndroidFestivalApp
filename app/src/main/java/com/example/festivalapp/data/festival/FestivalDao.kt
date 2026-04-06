package com.example.festivalapp.data.festival

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.festivalapp.data.festival.FestivalWithZones
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    
    // ========== SIMPLE QUERIES ==========
    @Query("SELECT * FROM festival")
    fun getAllFestivals(): Flow<List<FestivalEntity>>
    
    @Query("SELECT * FROM festival WHERE name = :name")
    fun getFestivalByName(name: String): Flow<FestivalEntity?>
    
    @Query("SELECT * FROM festival WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentFestival(): Flow<FestivalEntity?>
    
    // ========== WITH ZONES (TRANSACTION) ✅ ==========
    @Transaction
    @Query("SELECT * FROM festival")
    fun getAllFestivalsWithZones(): Flow<List<FestivalWithZones>>
    
    @Transaction
    @Query("SELECT * FROM festival WHERE name = :name")
    fun getFestivalWithZonesByName(name: String): Flow<FestivalWithZones?>
    
    @Transaction
    @Query("SELECT * FROM festival WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentFestivalWithZones(): Flow<FestivalWithZones?>
    
    // ========== WRITE OPERATIONS ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestival(festival: FestivalEntity)
    
    @Update
    suspend fun updateFestival(festival: FestivalEntity)
    
    @Delete
    suspend fun deleteFestival(festival: FestivalEntity)
    
    @Query("DELETE FROM festival WHERE name = :name")
    suspend fun deleteFestivalByName(name: String)
}