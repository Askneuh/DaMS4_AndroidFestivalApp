package com.example.festivalapp.data

import android.content.Context
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.datastore.UserPreferencesDs
import com.example.festivalapp.data.session.SessionRepository
import com.example.festivalapp.data.session.SESSION_PREFERENCE_NAME
import com.example.festivalapp.data.session.sessionDataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.festivalapp.data.user.room.OfflineUserRepository
import com.example.festivalapp.data.user.room.UserRepository

interface AppContainer {
    val UserRepository: UserRepository
    val authRepository: AuthRepository
    val userPreferences: UserPreferencesDs
    val sessionRepository: SessionRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineUserRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [UserRepository]
     */
    override val UserRepository: UserRepository by lazy {
        OfflineUserRepository(FestivalDatabase.getDatabase(context).userDAO())
    }
    override val userPreferences: UserPreferencesDs by lazy {
        UserPreferencesDs(context)
    }
    override val sessionRepository: SessionRepository by lazy {
        SessionRepository(context.sessionDataStore)
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitInstance.api, sessionRepository)
    }
}
