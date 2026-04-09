package com.example.festivalapp.ui.navigation
 
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

enum class ThemeMode { System, Light, Dark }
class NavigationModel {
    private val _backStack: SnapshotStateList<AppDestinations> = mutableStateListOf(AppDestinations.Login)
    val backStack: SnapshotStateList<AppDestinations>
        get() = _backStack
    fun navigateUp() {
        _backStack.removeAt(_backStack.lastIndex)
    }
    fun navigate(destination: AppDestinations) {
        _backStack.add(destination)
    }

     val _themeMode = mutableStateOf(ThemeMode.System)
    val themeMode: ThemeMode
        get() = _themeMode.value
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    private val isDrawerOpen = mutableStateOf(false)
    fun toggleDrawer() {
        isDrawerOpen.value = !isDrawerOpen.value
    }
    fun closeDrawer() {
        isDrawerOpen.value = false
    }
    fun openDrawer() {
        isDrawerOpen.value = true
    }
    fun isDrawerOpen(): Boolean {
        return isDrawerOpen.value
    }
}
