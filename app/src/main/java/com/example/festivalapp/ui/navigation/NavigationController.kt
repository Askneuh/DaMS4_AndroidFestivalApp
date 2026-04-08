package com.example.festivalapp.ui.navigation

import com.example.festivalapp.data.datastore.UserPreferencesDs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Controller manages user interactions to change the current view.
 * It commands the Model to update its state.
 */
class NavigationController(
    private val model: NavigationModel, 
    private val userPreferences: UserPreferencesDs,
    private val scope: CoroutineScope
) {

    fun navigateTo(destination: AppDestinations) {
        model.backStack.add(destination)
        closeDrawer()
    }

    fun navigateBack() {
        if (model.backStack.size > 1) {
            model.backStack.removeAt(model.backStack.lastIndex)
        }
    }

    fun setTheme(mode: ThemeMode) {
        model.themeMode.value = mode
        scope.launch {
            userPreferences.saveTheme(mode)
        }
    }

    fun toggleDrawer() {
        model.isDrawerOpen.value = !model.isDrawerOpen.value
    }

    fun closeDrawer() {
        model.isDrawerOpen.value = false
    }
}
