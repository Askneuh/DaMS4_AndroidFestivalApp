package com.example.festivalapp.ui.screen.admin

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.retrofit.UserRepository
import com.example.festivalapp.data.user.room.UserDto
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading: UiState()
    data class Success(val posts: List<UserDto>): UiState()
    data class Error(val message: String): UiState()
}


class AdminViewModel : ViewModel() {
    private val retrofitUserRepository = UserRepository()
    private var internalState : MutableState<UiState> = mutableStateOf(UiState.Loading)
    val state = internalState

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            internalState.value = UiState.Loading
            try {
                val posts = retrofitUserRepository.getUsers()
                internalState.value = UiState.Success(posts)
            } catch (e: Exception) {
                internalState.value = UiState.Error("Failed to load posts "+e.message)
            }
        }
    }
}