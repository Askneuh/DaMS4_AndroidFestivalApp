package com.example.festivalapp.ui.screen.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.editor.EditorRepository
import com.example.festivalapp.data.editor.room.Editor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface EditorListUiState {
    object Loading : EditorListUiState
    object Success : EditorListUiState
    data class Error(val message: String) : EditorListUiState
}

class EditorListViewModel(
    private val editorRepository: EditorRepository
) : ViewModel() {

    // --- État réseau (Loading / Success / Error) ---
    private val _networkState = MutableStateFlow<EditorListUiState>(EditorListUiState.Loading)
    val networkState: StateFlow<EditorListUiState> = _networkState

    // --- Filtres (modifiables par l'UI) ---
    val searchQuery = MutableStateFlow("")

    // --- Flux brut depuis Room ---
    private val _allEditors = editorRepository.getAllEditors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // --- Flux filtré (combine les données + les filtres en temps réel) ---
    val filteredItems: StateFlow<List<Editor>> =
        combine(_allEditors, searchQuery) { items, query ->
            items.filter { item ->
                query.isBlank() || item.name.contains(query, ignoreCase = true)
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
            _networkState.value = EditorListUiState.Loading
            try {
                editorRepository.refreshEditors()
                _networkState.value = EditorListUiState.Success
            } catch (e: Exception) {
                _networkState.value = EditorListUiState.Error("Erreur réseau: ${e.message}")
            }
        }
    }
}
