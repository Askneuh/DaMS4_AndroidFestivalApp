package com.example.festivalapp.ui.screen.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.reservation.room.EditorWithReservationTuple
import com.example.festivalapp.data.reservation.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val festivalName: String = "Festival-Nouveau"
) : ViewModel() {

    private val _networkState = MutableStateFlow<ReservationListUiState>(ReservationListUiState.Loading)
    val networkState = _networkState.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow("all")

    private val _allItems = reservationRepository.getEditorsWithReservations(festivalName)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

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

    fun createReservation(editorId: Int) {
        viewModelScope.launch {
            _networkState.value = ReservationListUiState.Loading
            try {
                reservationRepository.createReservation(editorId, festivalName)
                _networkState.value = ReservationListUiState.Success
            } catch (e: Exception) {
                _networkState.value = ReservationListUiState.Error("Erreur lors de la création : ${e.message}")
            }
        }
    }
}
