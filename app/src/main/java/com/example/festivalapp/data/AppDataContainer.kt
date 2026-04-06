package com.example.festivalapp.data

import android.content.Context
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.datastore.UserPreferencesDs
import com.example.festivalapp.data.festival.FestivalRepository
import com.example.festivalapp.data.user.room.OfflineUserRepository
import com.example.festivalapp.data.user.room.UserRepository

interface AppContainer {
    val UserRepository: UserRepository
    val authRepository: AuthRepository
    val apiService: APIService
    val festivalRepository: FestivalRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    init {
        RetrofitInstance.cookieJar = PersistentCookieJar(context)
    }

    override val UserRepository: UserRepository by lazy {
        OfflineUserRepository(FestivalDatabase.getDatabase(context).userDAO())
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitInstance.api, UserPreferencesDs(context))
    }

    override val apiService: APIService by lazy {
        RetrofitInstance.api
    }

    override val festivalRepository: FestivalRepository by lazy {
        FestivalRepository(
            FestivalDatabase.getDatabase(context).festivalDao(),
            FestivalDatabase.getDatabase(context).tariffZoneDao(),
            RetrofitInstance.api
        )
    }
}