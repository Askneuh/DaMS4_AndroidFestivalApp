package com.example.festivalapp.data.game.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)

    @Query("SELECT * FROM games ORDER BY name ASC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Int): Game?

    @Query("SELECT * FROM games WHERE idEditor = :idEditor ORDER BY name ASC")
    fun getGamesStreamByEditor(idEditor: Int): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchGames(query: String): List<Game>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)

    @Query("DELETE FROM games")
    suspend fun deleteAll()
}
