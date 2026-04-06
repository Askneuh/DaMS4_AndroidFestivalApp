package com.example.festivalapp.data

import android.content.Context
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.datastore.UserPreferencesDs
import com.example.festivalapp.data.festival.FestivalRepository
import com.example.festivalapp.data.reservation.room.ReservationRepository
import com.example.festivalapp.data.session.SessionRepository
import com.example.festivalapp.data.session.sessionDataStore
import com.example.festivalapp.data.user.room.DefaultUserRepository
import com.example.festivalapp.data.user.room.UserRepository
import com.example.festivalapp.data.editor.EditorRepository

interface AppContainer {
    val sessionRepository: SessionRepository
    val authRepository: AuthRepository
    val apiService: APIService
    val festivalRepository: FestivalRepository
    val userRepository: UserRepository
    val reservationRepository: ReservationRepository
    val userPreferences: UserPreferencesDs
    val editorRepository: EditorRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val sessionRepository: SessionRepository by lazy {
        SessionRepository(context.sessionDataStore)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitInstance.api, sessionRepository)
    }

    override val userRepository: UserRepository by lazy {
        DefaultUserRepository(
            userDAO = FestivalDatabase.getDatabase(context).userDAO(),
            api = RetrofitInstance.userApi
        )
    }

    override val reservationRepository: ReservationRepository by lazy {
        ReservationRepository(
            reservationDAO = FestivalDatabase.getDatabase(context).reservationDAO(),
            editorDAO = FestivalDatabase.getDatabase(context).editorDAO(),
            api = RetrofitInstance.reservationApi
        )
    }

    override val editorRepository: EditorRepository by lazy {
        EditorRepository(
            editorDAO = FestivalDatabase.getDatabase(context).editorDAO(),
            gameDAO = FestivalDatabase.getDatabase(context).gameDAO(),
            contactDAO = FestivalDatabase.getDatabase(context).contactDAO(),
            api = RetrofitInstance.editorApi
        )
    }

    override val apiService: APIService by lazy {
        RetrofitInstance.api
    }

    override val festivalRepository: FestivalRepository by lazy {
        FestivalRepository(
            festivalDao = FestivalDatabase.getDatabase(context).festivalDao(),
            tariffZoneDao = FestivalDatabase.getDatabase(context).tariffZoneDao(),
            planZoneDao = FestivalDatabase.getDatabase(context).planZoneDao(),
            apiService = RetrofitInstance.api
        )
    }

    override val userPreferences: UserPreferencesDs by lazy {
        UserPreferencesDs(context)
    }
}
