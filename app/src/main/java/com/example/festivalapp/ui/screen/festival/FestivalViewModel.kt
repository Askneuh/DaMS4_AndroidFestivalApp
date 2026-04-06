package com.example.festivalapp.ui.screen.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.festival.FestivalRepository
import com.example.festivalapp.data.festival.TariffZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FestivalViewModel(
    private val festivalRepository: FestivalRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FestivalUiState())
    val uiState: StateFlow<FestivalUiState> = _uiState.asStateFlow()

    init {
        loadAllFestivals()
    }

    // ========== LOAD DATA FROM REPOSITORY ==========

    private fun loadAllFestivals() {
        festivalRepository.getAllFestivals()
            .onEach { festivals ->
                _uiState.update { it.copy(festivals = festivals) }
            }
            .launchIn(viewModelScope)
    }

     fun loadCurrentFestival() {
        festivalRepository.getCurrentFestival()
            .onEach { festival ->
                _uiState.update { it.copy(currentFestival = festival) }
            }
            .launchIn(viewModelScope)
    }

    // ========== CREATE FESTIVAL ==========

    fun createFestival(festival: Festival) {
        viewModelScope.launch {
            setLoading(true)
            val result = festivalRepository.createFestival(festival)
            
            result.fold(
                onSuccess = { createdFestival ->
                    setSuccessMessage("Festival créé avec succès!")
                    closeForm()
                    loadAllFestivals()
                    loadCurrentFestival()
                },
                onFailure = { error ->
                    setError(error.message ?: "Erreur lors de la création")
                }
            )
            setLoading(false)
        }
    }

    // ========== UPDATE FESTIVAL ==========

    fun updateFestival(originalName: String, festival: Festival) {
        viewModelScope.launch {
            setLoading(true)
            val result = festivalRepository.updateFestival(originalName, festival)
            
            result.fold(
                onSuccess = { updatedFestival ->
                    setSuccessMessage("Festival mis à jour avec succès!")
                    closeForm()
                    loadAllFestivals()
                    loadCurrentFestival()
                },
                onFailure = { error ->
                    setError(error.message ?: "Erreur lors de la modification")
                }
            )
            setLoading(false)
        }
    }

    // ========== DELETE FESTIVAL ==========

    fun deleteFestival(name: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = festivalRepository.deleteFestival(name)
            
            result.fold(
                onSuccess = {
                    setSuccessMessage("Festival supprimé avec succès!")
                    loadAllFestivals()
                    loadCurrentFestival()
                },
                onFailure = { error ->
                    setError(error.message ?: "Erreur lors de la suppression")
                }
            )
            setLoading(false)
        }
    }

    // ========== SET CURRENT FESTIVAL ==========

    fun setCurrentFestival(name: String) {
        viewModelScope.launch {
            setLoading(true)
            val result = festivalRepository.setCurrentFestival(name)
            
            result.fold(
                onSuccess = { festival ->
                    setSuccessMessage("Le festival \"$name\" est désormais le festival courant!")
                    _uiState.update { it.copy(currentFestival = festival) }
                    loadAllFestivals()
                },
                onFailure = { error ->
                    setError(error.message ?: "Erreur")
                }
            )
            setLoading(false)
        }
    }

    // ========== PREPARE & CALCULATE ==========

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
            totalSmall += zone.nbSmallTables ?: 0
            totalLarge += zone.nbLargeTables ?: 0
            totalCityHall += zone.nbCityHallTables ?: 0
        }
        
        val zonesWithName = tariffZones.map { zone ->
            zone.copy(festivalName = name)
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
            tariffZones = zonesWithName,
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
            allocatedSmall += zone.nbSmallTables ?: 0
            allocatedLarge += zone.nbLargeTables ?: 0
            allocatedCityHall += zone.nbCityHallTables ?: 0
        }
        
        return mapOf(
            "remainingSmallTables" to maxOf(0, festival.nbSmallTables - allocatedSmall),
            "remainingLargeTables" to maxOf(0, festival.nbLargeTables - allocatedLarge),
            "remainingCityHallTables" to maxOf(0, festival.nbCityHallTables - allocatedCityHall)
        )
    }

    // ========== UI STATE MANAGEMENT ==========

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

    fun setSuccessMessage(message: String?) {
        _uiState.update { it.copy(successMessage = message) }
        if (message != null) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(successMessage = null) }
            }
        }
    }

    fun setFestivals(festivals: List<Festival>) {
        _uiState.update { it.copy(festivals = festivals) }
    }

    fun setCurrentFestivalState(festival: Festival?) {
        _uiState.update { it.copy(currentFestival = festival) }
    }
}