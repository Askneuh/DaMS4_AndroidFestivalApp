package com.example.festivalapp.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.datastore.UserPreferencesDs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: AuthRepository, private val userPreferences: UserPreferencesDs) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState = _loginState.asStateFlow()

    fun performLogin(user: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading
            val result = repository.login(user, pass)
            _loginState.value = result.fold(
                onSuccess = { token -> LoginResult.Success(token) },
                onFailure = { e -> LoginResult.Error(e.message ?: "Erreur inconnue") }
            )
        }
    }
}