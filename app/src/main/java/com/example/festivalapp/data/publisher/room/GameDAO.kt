package com.example.festivalapp.data.publisher.room

import com.example.festivalapp.data.festival.room.FestivalGameEntity
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
    // Global Games Registry
    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    // Specific Festival Games
    @Query("SELECT * FROM festival_games WHERE festivalId = :festivalId")
    fun getFestivalGames(festivalId: Int): Flow<List<FestivalGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFestivalGame(festivalGame: FestivalGameEntity)
}
