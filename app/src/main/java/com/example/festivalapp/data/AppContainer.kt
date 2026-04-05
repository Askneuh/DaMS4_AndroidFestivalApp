package com.example.festivalapp.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.reservation.room.ReservationRepository
import com.example.festivalapp.data.session.SessionRepository
import com.example.festivalapp.data.user.room.DefaultUserRepository
import com.example.festivalapp.data.user.room.UserRepository

private const val SESSION_PREFERENCE_NAME = "app_session"
private val Context.dataStore by preferencesDataStore(name = SESSION_PREFERENCE_NAME)

interface AppContainer {
    val sessionRepository: SessionRepository
    val authRepository: AuthRepository
    val userRepository: UserRepository
    val reservationRepository: ReservationRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val sessionRepository: SessionRepository by lazy {
        SessionRepository(context.dataStore)
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
            api = RetrofitInstance.reservationApi  // ← injecté ici
        )
    }
}