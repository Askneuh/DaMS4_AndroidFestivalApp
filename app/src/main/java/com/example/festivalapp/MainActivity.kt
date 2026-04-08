package com.example.festivalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
        
        val container = (application as FestivalApplication).container
        
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

            val themeMode by navigationModel.themeMode.collectAsState()
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
                }
            }
        }
    }
}
