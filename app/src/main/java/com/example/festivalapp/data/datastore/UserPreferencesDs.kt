package com.example.festivalapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.festivalapp.ui.navigation.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings_pref")

class UserPreferencesDs(private val context: Context) {

    // Définition de la clé pour le token
    companion object {
        val USER_THEME = stringPreferencesKey("user_theme")
    }

    suspend fun saveTheme(theme: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[USER_THEME] = theme.name
        }
    }

    val userTheme: Flow<ThemeMode> = context.settingsDataStore.data.map { preferences ->
        ThemeMode.valueOf(preferences[USER_THEME] ?: ThemeMode.System.name)
    }
}