package com.example.festivalapp.data.reservation.room

import com.example.festivalapp.data.RetrofitInstance
import com.example.festivalapp.data.editor.room.EditorDAO
import com.example.festivalapp.data.reservation.retrofit.ReservationApiService
import kotlinx.coroutines.flow.Flow

class ReservationRepository(
    private val reservationDAO: ReservationDAO,
    private val editorDAO: EditorDAO,
    private val api: ReservationApiService
) {

    fun getEditorsWithReservations(festivalName: String): Flow<List<EditorWithReservationTuple>> {
        return reservationDAO.getEditorsWithReservationsStatus(festivalName)
    }

    suspend fun refreshFromNetwork(festivalName: String) {
        val dtoList = api.getEditorsWithReservations(festivalName)
        val pairs = dtoList.map { it.toRoomEntities(festivalName) }
        editorDAO.insertAll(pairs.map { it.first })
        reservationDAO.clearForFestival(festivalName)
        val reservations = pairs.mapNotNull { it.second }
        reservationDAO.insertAll(reservations)
    }
}
