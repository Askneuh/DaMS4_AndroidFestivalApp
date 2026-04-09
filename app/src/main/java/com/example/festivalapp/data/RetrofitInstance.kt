package com.example.festivalapp.data

import com.example.festivalapp.data.auth.AuthApiService
import com.example.festivalapp.data.editor.retrofit.EditorApiService
import com.example.festivalapp.data.game.retrofit.GameApiService
import com.example.festivalapp.data.reservation.retrofit.reservation.ReservationApiService
import com.example.festivalapp.data.reservation.retrofit.suivi.SuiviApiService
import com.example.festivalapp.data.user.retrofit.UserApiService
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://162.38.111.35:4000/api/"

    lateinit var okHttpClient: OkHttpClient
    val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json;charset=utf-8".toMediaType()))
            .build()
    }

    val api : APIService by lazy {
        retrofit.create(APIService::class.java)
    }

    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val reservationApi: ReservationApiService by lazy {
        retrofit.create(ReservationApiService::class.java)
    }

    val gameApi: GameApiService by lazy {
        retrofit.create(GameApiService::class.java)
    }

    val suiviApi: SuiviApiService by lazy {
        retrofit.create(SuiviApiService::class.java)
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val editorApi: EditorApiService by lazy {
        retrofit.create(EditorApiService::class.java)
    }
}