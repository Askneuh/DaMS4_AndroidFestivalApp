package com.example.festivalapp.ui.screen.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.reservation.room.EditorWithReservationTuple
import com.example.festivalapp.data.reservation.room.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ReservationListUiState {
    object Loading : ReservationListUiState
    object Success : ReservationListUiState
    data class Error(val message: String) : ReservationListUiState
}

class ReservationListViewModel(
    private val reservationRepository: ReservationRepository,
    private val festivalName: String = "Festival-Nouveau" // Le festival courant est passé à l'initialisation
) : ViewModel() {

    // --- État réseau (Loading / Success / Error) ---
    private val _networkState = MutableStateFlow<ReservationListUiState>(ReservationListUiState.Loading)
    val networkState: StateFlow<ReservationListUiState> = _networkState

    // --- Filtres (modifiables par l'UI) ---
    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow("all")

    // --- Flux brut depuis Room ---
    private val _allItems = reservationRepository.getEditorsWithReservations(festivalName)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // --- Flux filtré (combine les données + les filtres en temps réel) ---
    val filteredItems: StateFlow<List<EditorWithReservationTuple>> =
        combine(_allItems, searchQuery, statusFilter) { items, query, status ->
            items
                .filter { item ->
                    query.isBlank() || item.editorName.contains(query, ignoreCase = true)
                }
                .filter { item ->
                    status == "all" || item.status == status
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _networkState.value = ReservationListUiState.Loading
            try {
                reservationRepository.refreshFromNetwork(festivalName)
                _networkState.value = ReservationListUiState.Success
            } catch (e: Exception) {
                _networkState.value = ReservationListUiState.Error("Erreur réseau: ${e.message}")
            }
        }
    }
}
