package com.example.festivalapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings_pref")

class UserPreferencesDs(private val context: Context) {

    // Définition de la clé pour le token
    companion object {
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    // Fonction pour sauvegarder le token (suspend car asynchrone)
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    // Lecture du token sous forme de Flow (réactif)
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN]
    }
}