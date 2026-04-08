package com.example.festivalapp.ui.navigation

sealed class AppDestinations(val isPublic: Boolean = false) {
    data object Home : AppDestinations(isPublic = true)
    data object Settings : AppDestinations()
    data object Login : AppDestinations(isPublic = true)

    // Festival Management
    data object FestivalList : AppDestinations()
    data class FestivalCreate(val festivalId: Int? = null) : AppDestinations()

    // Publisher Management
    data object PublisherList : AppDestinations()
    data class PublisherDetail(val publisherId: Int? = null) : AppDestinations()

    // Reservation & Workflow (Current Festival)
    data object ReservationOverview : AppDestinations(isPublic = true)
    data class ReservationDetail(val publisherId: Int) : AppDestinations()
    data object FestivalGamesList : AppDestinations(isPublic = true)
}


