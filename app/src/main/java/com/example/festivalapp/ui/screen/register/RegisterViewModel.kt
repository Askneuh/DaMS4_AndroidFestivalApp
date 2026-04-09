package com.example.festivalapp.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Loading : RegisterUiState()
    data class Success(val token: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUiState?>(null)
    val registerState = _registerState.asStateFlow()

    fun performRegister(user: String, pass: String) {
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            val result = repository.register(user, pass)
            _registerState.value = result.fold(
                onSuccess = { token -> RegisterUiState.Success(token) },
                onFailure = { e -> RegisterUiState.Error(e.message ?: "Erreur inconnue") }
            )
        }
    }
}