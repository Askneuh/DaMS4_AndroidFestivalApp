package com.example.festivalapp.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Loading : LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState?>(null)
    val loginState = _loginState.asStateFlow()

    fun performLogin(user: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            val result = repository.login(user, pass)
            _loginState.value = result.fold(
                onSuccess = { token -> LoginUiState.Success(token) },
                onFailure = { e -> LoginUiState.Error(e.message ?: "Erreur inconnue") }
            )
        }
    }
}