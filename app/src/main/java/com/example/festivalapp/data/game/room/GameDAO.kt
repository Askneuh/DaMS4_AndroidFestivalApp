package com.example.festivalapp.data.game.room

<<<<<<< HEAD
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
=======
import androidx.room.*
>>>>>>> 82c0fb8 (a lot of things)
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDAO {
<<<<<<< HEAD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

    @Query("SELECT * FROM games WHERE idEditor = :editorId ORDER BY name")
    fun getGamesByEditor(editorId: Int): Flow<List<Game>>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)
=======

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)


    @Query("SELECT * FROM games ORDER BY name ASC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Int): Game?

    @Query("SELECT * FROM games WHERE idEditor = :idEditor ORDER BY name ASC")
    suspend fun getGamesByEditor(idEditor: Int): List<Game>

    @Query("SELECT * FROM games WHERE idEditor != :idEditor ORDER BY name ASC")
    suspend fun getGamesNotByEditor(idEditor: Int): List<Game>

    @Query("SELECT * FROM games WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchGames(query: String): List<Game>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)

    @Query("DELETE FROM games")
    suspend fun deleteAll()


>>>>>>> 82c0fb8 (a lot of things)
}
