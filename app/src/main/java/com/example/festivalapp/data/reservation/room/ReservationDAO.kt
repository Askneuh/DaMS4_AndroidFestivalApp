package com.example.festivalapp.data.reservation.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reservations: List<Reservation>)

    @Query("DELETE FROM reservations WHERE festivalName = :festivalName")
    suspend fun clearForFestival(festivalName: String)

    // C'EST LA REQUETE LA PLUS IMPORTANTE !
    // Elle demande les Editeurs et colle leur réservation (gauche à droite) avec calcul manuel des "tables"
    @Query("""
        SELECT e.id as editorId, e.name as editorName, e.logo as editorLogo,
               r.idReservation, r.status, (r.nbSmallTables + r.nbLargeTables + r.nbCityHallTables) as totalTables
        FROM editors e
        LEFT JOIN reservations r ON e.id = r.idEditor AND r.festivalName = :festivalName
        ORDER BY e.name ASC
    """)
    fun getEditorsWithReservationsStatus(festivalName: String): Flow<List<EditorWithReservationTuple>>
}
