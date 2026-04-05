package com.example.festivalapp.data.reservation.retrofit

import retrofit2.http.*

interface ReservationApiService {

    // --- CELLE QU'ON UTILISE POUR L'ÉCRAN COURANT (issue du fichier editeurs.ts) ---
    @GET("editeurs/festival/{festivalName}/withReservationStatus")
    suspend fun getEditorsWithReservations(
        @Path("festivalName") festivalName: String
    ): List<EditorWithReservationDto>

    // --- TOUTES LES AUTRES ROUTES DU BACKEND (reservations.ts) ---

    // 1. Lire une réservation par ID
    @GET("reservations/{reservationId}")
    suspend fun getReservationById(@Path("reservationId") reservationId: Int): ReservationInnerDto

    // 2. Créer une réservation
    @POST("reservations")
    suspend fun createReservation(@Body request: Map<String, Any>): Map<String, Any> // Remplacer Map par un vrai DTO plus tard

    // 3. Mettre à jour une réservation
    @POST("reservations/update/{reservationId}")
    suspend fun updateReservation(
        @Path("reservationId") reservationId: Int,
        @Body updateRequest: Map<String, Any>
    ): Map<String, Any>

    // 4. Récupérer toutes les réservations d'un Éditeur précis
    @GET("reservations/byEditor/{idEditor}")
    suspend fun getReservationsByEditor(@Path("idEditor") idEditor: Int): List<ReservationInnerDto>

    // 5. Supprimer une réservation
    @DELETE("reservations/{reservationId}")
    suspend fun deleteReservation(@Path("reservationId") reservationId: Int)

    // --- GESTION DES JEUX DANS LA RÉSERVATION ---

    @GET("reservations/{reservationId}/games")
    suspend fun getReservationGames(@Path("reservationId") reservationId: Int): List<Map<String, Any>>

    @POST("reservations/{reservationId}/games")
    suspend fun addGameToReservation(
        @Path("reservationId") reservationId: Int,
        @Body gameRequest: Map<String, Any>
    )

    @PUT("reservations/{reservationId}/games/{gameId}")
    suspend fun updateReservationGame(
        @Path("reservationId") reservationId: Int,
        @Path("gameId") gameId: Int,
        @Body gameRequest: Map<String, Any>
    )

    @DELETE("reservations/{reservationId}/games/{gameId}")
    suspend fun removeGameFromReservation(
        @Path("reservationId") reservationId: Int,
        @Path("gameId") gameId: Int
    )
}
