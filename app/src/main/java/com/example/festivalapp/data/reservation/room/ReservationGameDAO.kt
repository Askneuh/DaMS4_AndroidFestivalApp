package com.example.festivalapp.data.reservation.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Objet simple pour récupérer les infos du jeu jointes à la réservation
 */
data class GameWithReservationInfo(
    val id: Int,
    val name: String,
    val author: String?,
    val gameImage: String?,
    val quantity: Int,
    val isGamePlaced: Boolean
)

@Dao
interface ReservationGameDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(links: List<ReservationGame>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(link: ReservationGame)

    @Query("""
        SELECT g.id, g.name, g.author, g.gameImage, rg.quantity, rg.isGamePlaced
        FROM reservation_game rg
        INNER JOIN games g ON rg.idGame = g.id
        WHERE rg.idReservation = :reservationId
        ORDER BY g.name ASC
    """)
    fun getGamesForReservation(reservationId: Int): Flow<List<GameWithReservationInfo>>

    @Query("DELETE FROM reservation_game WHERE idReservation = :reservationId AND idGame = :gameId")
    suspend fun deleteGameFromReservation(reservationId: Int, gameId: Int)

    @Query("DELETE FROM reservation_game WHERE idReservation = :reservationId")
    suspend fun deleteAllGamesForReservation(reservationId: Int)


    @Transaction
    suspend fun replaceAllGamesForReservation(reservationId: Int, games: List<ReservationGame>) {
        deleteAllGamesForReservation(reservationId)
        insertAll(games)
    }

    @Query("UPDATE reservation_game SET quantity = :quantity, isGamePlaced = :isPlaced WHERE idReservation = :reservationId AND idGame = :gameId")
    suspend fun updateGameQuantityAndPlacement(reservationId: Int, gameId: Int, quantity: Int, isPlaced: Boolean)


}
