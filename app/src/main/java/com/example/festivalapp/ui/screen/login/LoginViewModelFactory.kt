package com.example.festivalapp.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.data.datastore.UserPreferencesDs

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val prefs: UserPreferencesDs
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository, prefs) as T
    }
}