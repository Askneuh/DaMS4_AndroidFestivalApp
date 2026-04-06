package com.example.festivalapp

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.festivalapp.ui.screen.admin.users.AdminUserListViewModel
import com.example.festivalapp.ui.screen.login.LoginViewModel
import com.example.festivalapp.ui.screen.reservation.ReservationDetailViewModel
import com.example.festivalapp.ui.screen.reservation.ReservationListViewModel
import com.example.festivalapp.ui.screen.festival.FestivalViewModel
import com.example.festivalapp.ui.screen.editor.EditorListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            LoginViewModel(repository = festivalApplication().container.authRepository)
        }

        initializer {
            AdminUserListViewModel(userRepository = festivalApplication().container.userRepository)
        }

        initializer {
            ReservationListViewModel(
                reservationRepository = festivalApplication().container.reservationRepository,
                festivalRepository = festivalApplication().container.festivalRepository
            )
        }

        initializer {
            FestivalViewModel(festivalRepository = festivalApplication().container.festivalRepository)
        }

        initializer {
            EditorListViewModel(editorRepository = festivalApplication().container.editorRepository)
        }

        initializer {
            ReservationDetailViewModel(
                reservationRepository = festivalApplication().container.reservationRepository,
                gameRepository = festivalApplication().container.gameRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.festivalApplication(): FestivalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FestivalApplication)
