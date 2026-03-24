package com.example.festivalapp.ui.screen.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.festival.TariffZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FestivalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FestivalUiState())
    val uiState: StateFlow<FestivalUiState> = _uiState.asStateFlow()

    // ========== MÉTIERS ==========

    fun prepareFestivalForSave(
        name: String,
        tariffZones: List<TariffZone>,
        beginDate: String? = null,
        endDate: String? = null
    ): Festival {
        var totalSmall = 0
        var totalLarge = 0
        var totalCityHall = 0

        for (zone in tariffZones) {
            totalSmall += zone.nbSmallTables
            totalLarge += zone.nbLargeTables
            totalCityHall += zone.nbCityHallTables
        }

        return Festival(
            name = name,
            nbSmallTables = totalSmall,
            nbLargeTables = totalLarge,
            nbCityHallTables = totalCityHall,
            remainingSmallTables = totalSmall,
            remainingLargeTables = totalLarge,
            remainingCityHallTables = totalCityHall,
            isCurrent = false,
            tariffZones = tariffZones,
            beginDate = beginDate,
            endDate = endDate
        )
    }

    fun calculateRemainingTables(festival: Festival): Map<String, Int> {
        if (festival.tariffZones.isEmpty()) {
            return mapOf(
                "remainingSmallTables" to festival.nbSmallTables,
                "remainingLargeTables" to festival.nbLargeTables,
                "remainingCityHallTables" to festival.nbCityHallTables
            )
        }

        var allocatedSmall = 0
        var allocatedLarge = 0
        var allocatedCityHall = 0

        festival.tariffZones.forEach { zone ->
            allocatedSmall += zone.nbSmallTables
            allocatedLarge += zone.nbLargeTables
            allocatedCityHall += zone.nbCityHallTables
        }

        return mapOf(
            "remainingSmallTables" to maxOf(0, festival.nbSmallTables - allocatedSmall),
            "remainingLargeTables" to maxOf(0, festival.nbLargeTables - allocatedLarge),
            "remainingCityHallTables" to maxOf(0, festival.nbCityHallTables - allocatedCityHall)
        )
    }

    // ========== STATE MANAGEMENT ==========

    fun openForm(festival: Festival? = null) {
        _uiState.update {
            it.copy(
                showForm = true,
                isEditMode = festival != null,
                selectedFestival = festival
            )
        }
    }

    fun closeForm() {
        _uiState.update {
            it.copy(
                showForm = false,
                isEditMode = false,
                selectedFestival = null,
                error = null
            )
        }
    }

    fun setLoading(loading: Boolean) {
        _uiState.update { it.copy(isLoading = loading) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    fun setFestivals(festivals: List<Festival>) {
        _uiState.update { it.copy(festivals = festivals) }
    }

    fun setCurrentFestival(festival: Festival?) {
        _uiState.update { it.copy(currentFestival = festival) }
    }

    fun setSuccessMessage(message: String?) {
        _uiState.update { it.copy(successMessage = message) }
        if (message != null) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(successMessage = null) }
            }
        }
    }
}