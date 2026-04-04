package com.example.festivalapp.data

import android.content.Context
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.session.SessionRepository
import com.example.festivalapp.data.user.room.OfflineUserRepository
import com.example.festivalapp.data.user.room.UserRepository

interface AppContainer {
    val UserRepository: UserRepository
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
}