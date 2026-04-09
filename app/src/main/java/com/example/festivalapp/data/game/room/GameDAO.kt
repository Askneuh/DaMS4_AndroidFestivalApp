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

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)

    // Specific Festival Games
    @Query("SELECT * FROM festival_games WHERE festivalId = :festivalId")
    fun getFestivalGames(festivalId: Int): Flow<List<com.example.festivalapp.data.festival.room.FestivalGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestivalGame(festivalGame: com.example.festivalapp.data.festival.room.FestivalGameEntity)
}
