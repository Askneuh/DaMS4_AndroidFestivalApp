package com.example.festivalapp.data

import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import retrofit2.converter.kotlinx.serialization.asConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "https://162.38.111.35:4000/api/"

    lateinit var okHttpClient: OkHttpClient
    val json = Json {
        ignoreUnknownKeys = true // Ignore les champs JSON non présents dans votre data class [cite: 54]
    }
    val api : APIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json;charset=utf-8".toMediaType()))
            .build()
            .create(APIService::class.java)
    }

}