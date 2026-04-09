package com.example.festivalapp.data.reservation.room

data class EditorWithReservationTuple(
    val editorId: Int,
    val editorName: String,
    val editorLogo: String?,
    val idReservation: Int?,
    val status: String?,
    val totalTables: Int?
)
