package com.example.festivalapp.ui.navigation
 
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

enum class ThemeMode { System, Light, Dark }
class NavigationModel {
    val backStack: SnapshotStateList<AppDestinations> = mutableStateListOf(AppDestinations.Login)
    val themeMode = mutableStateOf(ThemeMode.System)
    val isDrawerOpen = mutableStateOf(false)
}
