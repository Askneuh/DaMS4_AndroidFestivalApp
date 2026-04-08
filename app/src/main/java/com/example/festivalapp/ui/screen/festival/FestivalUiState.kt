package com.example.festivalapp.ui.screen.festival

import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.festival.TariffZone

data class FestivalUiState(
    val festivals: List<Festival> = emptyList(),
    val currentFestival: Festival? = null,
    val selectedFestival: Festival? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val isEditMode: Boolean = false,
    val successMessage: String? = null
)