package com.example.festivalapp.data.reservation.retrofit.reservation

import com.example.festivalapp.data.reservation.retrofit.AddGameRequest
import com.example.festivalapp.data.reservation.retrofit.CreateResponse
import com.example.festivalapp.data.reservation.retrofit.EditorWithReservationDto
import com.example.festivalapp.data.reservation.retrofit.MessageResponse
import com.example.festivalapp.data.reservation.retrofit.UpdateGameRequest
import com.example.festivalapp.data.reservation.retrofit.UpdateReservationRequest
import retrofit2.http.*

interface ReservationApiService {

    @GET("editeurs/festival/{festivalName}/withReservationStatus")
    suspend fun getEditorsWithReservations(
        @Path("festivalName") festivalName: String
    ): List<EditorWithReservationDto>

    @GET("reservation/{reservationId}")
    suspend fun getReservationById(@Path("reservationId") reservationId: Int): ReservationDetailDto

    @POST("reservation")
    suspend fun createReservation(@Body request: CreateReservationRequest): CreateResponse

    @POST("reservation/update/{reservationId}")
    suspend fun updateReservation(
        @Path("reservationId") reservationId: Int,
        @Body request: UpdateReservationRequest
    ): MessageResponse

    @GET("reservation/byEditor/{idEditor}")
    suspend fun getByEditor(@Path("idEditor") idEditor: Int): List<ReservationDetailDto>

    @GET("reservation/byFestival/{festivalName}")
    suspend fun getByFestival(@Path("festivalName") festivalName: String): List<ReservationDetailDto>

    @GET("reservation/{reservationId}/games")
    suspend fun getReservationGames(@Path("reservationId") reservationId: Int): List<ReservationGameDto>

    @POST("reservation/{reservationId}/games")
    suspend fun addGame(@Path("reservationId") reservationId: Int, @Body request: AddGameRequest): MessageResponse

    @DELETE("reservation/{reservationId}/games/{gameId}")
    suspend fun removeGame(@Path("reservationId") reservationId: Int, @Path("gameId") gameId: Int): MessageResponse

    @DELETE("reservation/{reservationId}")
    suspend fun deleteReservation(@Path("reservationId") reservationId: Int): MessageResponse

    @PUT("reservation/{reservationId}/games/{gameId}")
    suspend fun updateGameInReservation(
        @Path("reservationId") reservationId: Int,
        @Path("gameId") gameId: Int,
        @Body request: UpdateGameRequest
    ): MessageResponse

}
