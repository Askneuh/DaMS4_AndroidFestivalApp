package com.example.festivalapp.ui.screen.login

sealed class LoginResult {
    object Loading : LoginResult()
    data class Success(val token: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}