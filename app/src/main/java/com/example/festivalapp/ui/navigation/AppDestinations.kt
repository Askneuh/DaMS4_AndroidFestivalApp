package com.example.festivalapp.ui.navigation

sealed class AppDestinations(val isPublic: Boolean = false) {
    data object Home : AppDestinations(isPublic = true)
    data object Settings : AppDestinations()
    data object Login : AppDestinations(isPublic = true)

    // Festival Management
    data object FestivalList : AppDestinations()
    data class FestivalCreate(val festivalId: String? = null) : AppDestinations()

    // Editor Management (Replaces Publisher)
    data object EditorList : AppDestinations()
    data class EditorDetail(val editorId: Int? = null) : AppDestinations()

    // Reservation & Workflow (Current Festival)
    data object ReservationOverview : AppDestinations(isPublic = true)
    data class ReservationDetail(val editorId: Int) : AppDestinations()
    data object FestivalGamesList : AppDestinations(isPublic = true)
}
