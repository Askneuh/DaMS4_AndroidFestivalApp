package com.example.festivalapp.data.reservation.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reservations: List<Reservation>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE idReservation = :id")
    fun getReservationById(id: Int): Flow<Reservation?>

    @Query("DELETE FROM reservations WHERE festivalName = :festivalName")
    suspend fun clearForFestival(festivalName: String)

    @Query("""
        SELECT e.id as editorId, e.name as editorName, e.logo as editorLogo,
               r.idReservation, r.status, 
               (COALESCE(r.nbSmallTables, 0) + COALESCE(r.nbLargeTables, 0) + COALESCE(r.nbCityHallTables, 0)) as totalTables
        FROM editors e
        LEFT JOIN reservations r ON e.id = r.idEditor AND r.festivalName = :festivalName
        ORDER BY e.name ASC
    """)
    fun getEditorsWithReservationsStatus(festivalName: String): Flow<List<EditorWithReservationTuple>>
}
