package com.example.festivalapp.ui.screen.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UserListUiState {
    object Loading : UserListUiState
    object Success : UserListUiState
    data class Error(val message: String) : UserListUiState
}

class AdminUserListViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _networkState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val networkState = _networkState.asStateFlow()

    val localUsersState: StateFlow<List<User>> = userRepository.getAllUserStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        refreshList()
    }

    fun refreshList() {
        viewModelScope.launch {
            _networkState.value = UserListUiState.Loading
            try {
                userRepository.refreshUsers()
                _networkState.value = UserListUiState.Success
            } catch (e: Exception) {
                _networkState.value = UserListUiState.Error("Erreur réseau: ${e.message}")
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            _networkState.value = UserListUiState.Loading
            try {
                userRepository.deleteUser(user.id)
                _networkState.value = UserListUiState.Success
            } catch (e: Exception) {
                _networkState.value = UserListUiState.Error("Erreur réseau : impossible de supprimer l'utilisateur")
            }
        }
    }

    fun updateRole(userId: Int, newRole: String) {
        viewModelScope.launch {
            _networkState.value = UserListUiState.Loading
            try {
                userRepository.updateUserRole(userId, newRole)
                _networkState.value = UserListUiState.Success
            } catch (e: Exception) {
                _networkState.value = UserListUiState.Error("Erreur réseau : impossible de modifier le rôle")
            }
        }
    }
}
