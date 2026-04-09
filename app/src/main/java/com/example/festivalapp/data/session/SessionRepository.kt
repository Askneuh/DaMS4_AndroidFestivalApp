package com.example.festivalapp.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.preferencesDataStore

const val SESSION_PREFERENCE_NAME = "app_session"
val Context.sessionDataStore by preferencesDataStore(name = SESSION_PREFERENCE_NAME)

class SessionRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    val accessCookieFlow: Flow<String?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            prefs  -> prefs[ACCESS_TOKEN]
        }

    val refreshCookieFlow: Flow<String?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            prefs -> prefs[REFRESH_TOKEN]
        }

    val roleFlow: Flow<String?> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            prefs -> prefs[USER_ROLE]
        }

    suspend fun saveAccessCookie(value: String) {
        dataStore.edit {preferences -> preferences[ACCESS_TOKEN] = value}
    }

    suspend fun saveRefreshCookie(value: String) {
        dataStore.edit {preferences -> preferences[REFRESH_TOKEN] = value}
    }

    suspend fun saveRole(value: String) {
        dataStore.edit {preferences -> preferences[USER_ROLE] = value}
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}