package com.example.festivalapp

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.festivalapp.ui.screen.admin.users.AdminUserListViewModel
import com.example.festivalapp.ui.screen.login.LoginViewModel
import com.example.festivalapp.ui.screen.reservation.ReservationListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            LoginViewModel(repository = festivalApplication().container.authRepository,)
        }

        initializer {
            AdminUserListViewModel(userRepository = festivalApplication().container.userRepository)
        }

        initializer { ReservationListViewModel(reservationRepository = festivalApplication().container.reservationRepository) }
    }
}

// Petite fonction magique pour ne pas répéter "récupère moi l'application" partout (Typique des tutoriels Google)
fun CreationExtras.festivalApplication(): FestivalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FestivalApplication)
