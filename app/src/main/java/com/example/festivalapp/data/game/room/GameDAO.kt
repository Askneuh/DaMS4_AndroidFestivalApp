package com.example.festivalapp.data.game.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

    @Query("SELECT * FROM games WHERE idEditor = :editorId ORDER BY name")
    fun getGamesByEditor(editorId: Int): Flow<List<Game>>
}
