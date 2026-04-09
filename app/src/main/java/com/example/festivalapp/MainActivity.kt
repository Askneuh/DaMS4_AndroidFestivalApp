package com.example.festivalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.festivalapp.ui.navigation.AppNavigation
import com.example.festivalapp.ui.navigation.NavigationController
import com.example.festivalapp.ui.navigation.NavigationModel
import com.example.festivalapp.ui.navigation.ThemeMode
import com.example.festivalapp.ui.theme.FestivalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        
        val container = (application as FestivalApplication).container
=======
        val app = application as FestivalApplication

>>>>>>> 82c0fb8 (a lot of things)

        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val navigationModel = remember { NavigationModel() }
            val navigationController = remember(navigationModel) {
                NavigationController(
                    navigationModel,
                    container.userPreferences,
                    scope
                )
            }

<<<<<<< HEAD
            val themeMode = navigationModel.themeMode
            val darkTheme = when (themeMode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            FestivalAppTheme(darkTheme = darkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(
                            model = navigationModel,
                            controller = navigationController,
                            userPreferences = container.userPreferences,
                            authRepository = container.authRepository,
                            sessionRepository = container.sessionRepository
                        )
                    }
=======
                if (role != null) {
                    val scope = rememberCoroutineScope()
                    var selectedReservationId by remember { mutableStateOf<Int?>(null) }

                    if (selectedReservationId == null) {
                        ReservationListRoute(
                            reservationRepository = app.container.reservationRepository,
                            festivalName = "Festival-Nouveau",
                            onLogoutClick = {
                                scope.launch { app.container.sessionRepository.clearSession() }
                            },
                            onReservationClick = { id -> selectedReservationId = id }
                        )
                    } else {
                        ReservationDetailRoute(
                            reservationId = selectedReservationId!!,
                            reservationRepository = app.container.reservationRepository,
                            onBack = { selectedReservationId = null }
                        )
                    }

                } else {
                    LoginRoute(
                        onLoginSuccess = {}
                    )
>>>>>>> 82c0fb8 (a lot of things)
                }
            }
        }
    }
}
