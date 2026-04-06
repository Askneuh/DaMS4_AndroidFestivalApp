package com.example.festivalapp.data.reservation.retrofit.suivi

import retrofit2.http.*

interface SuiviApiService {

    @GET("suiviReservation/reservation/{reservationId}")
    suspend fun getByReservation(@Path("reservationId") reservationId: Int): List<SuiviReservationDto>

    @GET("suiviReservation/{id}")
    suspend fun getById(@Path("id") id: Int): SuiviReservationDto

    @POST("suiviReservation")
    suspend fun create(@Body request: CreateSuiviRequest): SuiviReservationDto

    @DELETE("suiviReservation/{id}")
    suspend fun delete(@Path("id") id: Int)

    @DELETE("suiviReservation/reservation/{reservationId}")
    suspend fun deleteByReservation(@Path("reservationId") reservationId: Int)
}
