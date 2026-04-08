package com.example.festivalapp.data.reservation.room

// Remarque : Ce n'est PAS une @Entity ! C'est juste un conteneur personnalisé.
data class EditorWithReservationTuple(
    val editorId: Int,
    val editorName: String,
    val editorLogo: String?,
    // Ces données seront "nulles" (null) si l'éditeur n'a pas encore de réservation
    val idReservation: Int?,
    val status: String?,
    val totalTables: Int?
)
