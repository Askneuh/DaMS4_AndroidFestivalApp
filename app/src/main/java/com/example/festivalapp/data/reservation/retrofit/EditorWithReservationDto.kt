package com.example.festivalapp.data.reservation.retrofit

import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.reservation.room.Reservation
import kotlinx.serialization.Serializable

@Serializable
data class EditorWithReservationDto(
    val id: Int,
    val name: String,
    val exposant: Boolean,
    val distributeur: Boolean,
    val logo: String?,
    val hasReservation: Boolean,
    val reservation: ReservationInnerDto?
) {
    fun toRoomEntities(festivalName: String): Pair<Editor, Reservation?> {
        val editor = Editor(id = id, name = name, exposant = exposant, distributeur = distributeur, logo = logo)

        val res = if (reservation != null) {
            Reservation(
                idReservation = reservation.idReservation,
                idEditor = id,
                festivalName = festivalName,
                status = reservation.status,
                nbSmallTables = reservation.nbSmallTables,
                nbLargeTables = reservation.nbLargeTables,
                nbCityHallTables = reservation.nbCityHallTables,
                m2 = reservation.m2,
                remise = reservation.remise,
                idTZ = null,
                typeAnimateur = 0,
                listeDemandee = false,
                listeRecue = false,
                jeuxRecus = false
            )
        } else null

        return Pair(editor, res)
    }
}

@Serializable
data class ReservationInnerDto(
    val idReservation: Int,
    val status: String,
    val nbSmallTables: Int,
    val nbLargeTables: Int,
    val nbCityHallTables: Int,
    val m2: Int,
    val remise: Double,
    val totalTables: Int,
    val totalPrice: Double,
    val lastContactDate: String?
)
