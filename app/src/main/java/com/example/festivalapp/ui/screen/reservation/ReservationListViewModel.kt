package com.example.festivalapp.ui.screen.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.reservation.room.EditorWithReservationTuple
import com.example.festivalapp.data.reservation.room.ReservationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.festivalapp.data.festival.FestivalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

sealed interface ReservationListUiState {
    object Loading : ReservationListUiState
    object Success : ReservationListUiState
    data class Error(val message: String) : ReservationListUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationListViewModel(
    private val reservationRepository: ReservationRepository,
    private val festivalRepository: FestivalRepository
) : ViewModel() {

    // --- État réseau (Loading / Success / Error) ---
    private val _networkState = MutableStateFlow<ReservationListUiState>(ReservationListUiState.Loading)
    val networkState: StateFlow<ReservationListUiState> = _networkState

    // --- Filtres (modifiables par l'UI) ---
    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow("all")

    // --- Flux du nom du festival courant ---
    val currentFestivalName: StateFlow<String> = festivalRepository.getCurrentFestival()
        .filterNotNull()
        .map { it.name }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ""
        )

    // --- Flux brut depuis Room (réagit au changement de nom) ---
    private val _allItems = currentFestivalName.flatMapLatest { name ->
        if (name.isBlank()) flowOf(emptyList())
        else reservationRepository.getEditorsWithReservations(name)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // --- Flux filtré ---
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
        // Au démarrage et à chaque changement de festival, on tente un refresh
        currentFestivalName.onEach { name ->
            if (name.isNotBlank()) {
                refresh(name)
            }
        }.launchIn(viewModelScope)
    }

    fun refresh(name: String? = null) {
        val targetName = name ?: currentFestivalName.value
        if (targetName.isBlank()) return

        viewModelScope.launch {
            _networkState.value = ReservationListUiState.Loading
            try {
                reservationRepository.refreshFromNetwork(targetName)
                _networkState.value = ReservationListUiState.Success
            } catch (e: Exception) {
                _networkState.value = ReservationListUiState.Error("Erreur réseau: ${e.message}")
            }
        }
    }
}
