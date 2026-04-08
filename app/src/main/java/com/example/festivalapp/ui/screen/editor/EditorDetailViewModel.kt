package com.example.festivalapp.ui.screen.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.contact.room.Contact
import com.example.festivalapp.data.editor.room.EditorRepository
import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.editor.retrofit.CreateContactRequest
import com.example.festivalapp.data.editor.retrofit.CreateGameRequest
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

    fun addGame(
        name: String,
        author: String,
        nbMinPlayer: Int,
        nbMaxPlayer: Int,
        minimumAge: Int,
        duration: Int
    ) {
        viewModelScope.launch {
            try {
                val request = CreateGameRequest(
                    name = name,
                    author = author,
                    nbMinPlayer = nbMinPlayer,
                    nbMaxPlayer = nbMaxPlayer,
                    minimumAge = minimumAge,
                    duration = duration,
                    idEditor = editorId,
                    idGameType = 1,
                    prototype = false
                )
                editorRepository.addGame(request)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: retrofit2.HttpException) {
                val errorMsg = try {
                    val jsonObj = org.json.JSONObject(e.response()?.errorBody()?.string() ?: "")
                    jsonObj.getString("error")
                } catch (ex: Exception) {
                    e.message()
                }
                _networkState.value = EditorDetailUiState.Error("Ajout refusé: $errorMsg")
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur lors de l'ajout: ${e.message}")
            }
        }
    }

    fun updateGame(
        gameId: Int,
        name: String,
        author: String,
        nbMinPlayer: Int,
        nbMaxPlayer: Int,
        minimumAge: Int,
        duration: Int
    ) {
        viewModelScope.launch {
            try {
                val request = CreateGameRequest(
                    name = name,
                    author = author,
                    nbMinPlayer = nbMinPlayer,
                    nbMaxPlayer = nbMaxPlayer,
                    minimumAge = minimumAge,
                    duration = duration,
                    idEditor = editorId,
                    idGameType = 1,
                    prototype = false
                )
                editorRepository.updateGame(gameId, request)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: retrofit2.HttpException) {
                val errorMsg = try {
                    val jsonObj = org.json.JSONObject(e.response()?.errorBody()?.string() ?: "")
                    jsonObj.getString("error")
                } catch (ex: Exception) {
                    e.message()
                }
                _networkState.value = EditorDetailUiState.Error("Modification refusée: $errorMsg")
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur lors de la modification: ${e.message}")
            }
        }
    }

    fun deleteGame(gameId: Int) {
        viewModelScope.launch {
            try {
                editorRepository.deleteGame(gameId, editorId)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur lors de la suppression: ${e.message}")
            }
        }
    }

    fun addContact(
        name: String,
        email: String,
        phone: String?,
        role: String?,
        priority: Boolean
    ) {
        viewModelScope.launch {
            try {
                val request = CreateContactRequest(
                    name = name,
                    email = email,
                    idEditor = editorId,
                    phone = phone?.takeIf { it.isNotBlank() },
                    role = role?.takeIf { it.isNotBlank() },
                    priority = priority
                )
                editorRepository.addContact(request)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: retrofit2.HttpException) {
                val errorMsg = try {
                    val jsonObj = org.json.JSONObject(e.response()?.errorBody()?.string() ?: "")
                    jsonObj.getString("error")
                } catch (ex: Exception) {
                    e.message()
                }
                _networkState.value = EditorDetailUiState.Error("Ajout refusé: $errorMsg")
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur lors de l'ajout: ${e.message}")
            }
        }
    }

    fun updateContact(
        contactId: Int,
        name: String,
        email: String,
        phone: String?,
        role: String?,
        priority: Boolean
    ) {
        viewModelScope.launch {
            try {
                val request = CreateContactRequest(
                    name = name,
                    email = email,
                    idEditor = editorId,
                    phone = phone?.takeIf { it.isNotBlank() },
                    role = role?.takeIf { it.isNotBlank() },
                    priority = priority
                )
                editorRepository.updateContact(contactId, request)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: retrofit2.HttpException) {
                val errorMsg = try {
                    val jsonObj = org.json.JSONObject(e.response()?.errorBody()?.string() ?: "")
                    jsonObj.getString("error")
                } catch (ex: Exception) {
                    e.message()
                }
                _networkState.value = EditorDetailUiState.Error("Modification refusée: $errorMsg")
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur lors de la modification: ${e.message}")
            }
        }
    }

    fun deleteContact(contactId: Int) {
        viewModelScope.launch {
            try {
                editorRepository.deleteContact(contactId, editorId)
                _networkState.value = EditorDetailUiState.Success
            } catch (e: Exception) {
                _networkState.value = EditorDetailUiState.Error("Erreur lors de la suppression du contact: ${e.message}")
            }
        }
    }
}

