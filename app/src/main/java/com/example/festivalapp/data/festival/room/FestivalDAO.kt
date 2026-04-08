package com.example.festivalapp.data.festival.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDAO {
    @Query("SELECT * FROM festivals")
    fun getAllFestivals(): Flow<List<FestivalEntity>>

    @Query("SELECT * FROM festivals WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentFestival(): Flow<FestivalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestival(festival: FestivalEntity)

    @Query("UPDATE festivals SET isCurrent = 0")
    suspend fun clearCurrentFestival()

    @Query("UPDATE festivals SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrentFestival(id: Int)
}
