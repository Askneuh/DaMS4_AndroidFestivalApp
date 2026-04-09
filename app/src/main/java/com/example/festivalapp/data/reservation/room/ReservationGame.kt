package com.example.festivalapp.data.reservation.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.festivalapp.data.game.room.Game

@Entity(
    tableName = "reservation_game",
    primaryKeys = ["idReservation", "idGame"],
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["idReservation"],
            childColumns = ["idReservation"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["idGame"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idReservation"]),
        Index(value = ["idGame"])
    ]
)
data class ReservationGame(
    val idReservation: Int,
    val idGame: Int,
    val isGamePlaced: Boolean = false,
    val quantity: Int = 1
)
