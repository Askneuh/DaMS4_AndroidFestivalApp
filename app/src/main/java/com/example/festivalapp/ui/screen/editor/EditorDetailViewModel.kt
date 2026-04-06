package com.example.festivalapp.ui.screen.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.contact.room.Contact
import com.example.festivalapp.data.editor.EditorRepository
import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.game.room.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface EditorDetailUiState {
    object Loading : EditorDetailUiState
    object Success : EditorDetailUiState
    data class Error(val message: String) : EditorDetailUiState
}

class EditorDetailViewModel(
    private val editorRepository: EditorRepository,
    private val editorId: Int
) : ViewModel() {

    private val _networkState = MutableStateFlow<EditorDetailUiState>(EditorDetailUiState.Loading)
    val networkState: StateFlow<EditorDetailUiState> = _networkState

    val editor: StateFlow<Editor?> = editorRepository.getEditorById(editorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    val games: StateFlow<List<Game>> = editorRepository.getGamesByEditor(editorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val contacts: StateFlow<List<Contact>> = editorRepository.getContactsByEditor(editorId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _networkState.value = EditorDetailUiState.Loading
            try {
                editorRepository.refreshGamesForEditor(editorId)
                editorRepository.refreshContactsForEditor(editorId)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur réseau: ${e.message}")
            }
        }
    }
}
