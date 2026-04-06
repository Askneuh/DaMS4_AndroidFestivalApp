package com.example.festivalapp.data.reservation.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SuiviReservationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suivis: List<SuiviReservation>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(suivi: SuiviReservation)

    @Query("SELECT * FROM suivi_reservation WHERE idReservation = :reservationId ORDER BY date DESC")
    fun getSuivisByReservation(reservationId: Int): Flow<List<SuiviReservation>>

    @Query("SELECT * FROM suivi_reservation WHERE id = :id")
    suspend fun getSuiviById(id: Int): SuiviReservation?


    @Query("DELETE FROM suivi_reservation WHERE id = :id")
    suspend fun deleteSuiviById(id: Int)

    @Query("DELETE FROM suivi_reservation WHERE idReservation = :reservationId")
    suspend fun deleteSuivisByReservation(reservationId: Int)

    @Query("DELETE FROM suivi_reservation")
    suspend fun deleteAll()
}
