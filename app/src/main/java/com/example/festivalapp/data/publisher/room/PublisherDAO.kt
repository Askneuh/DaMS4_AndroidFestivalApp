package com.example.festivalapp.data.publisher.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PublisherDAO {
    @Query("SELECT * FROM publishers")
    fun getAllPublishers(): Flow<List<PublisherEntity>>

    @Query("SELECT * FROM publishers WHERE festivalId = :festivalId")
    fun getPublishersByFestival(festivalId: Int): Flow<List<PublisherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublisher(publisher: PublisherEntity)
}
