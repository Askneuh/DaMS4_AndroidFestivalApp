package com.example.festivalapp

import android.content.Context
import com.example.festivalapp.ui.navigation.AppNavigation
import com.example.festivalapp.ui.navigation.NavigationModel
import com.example.festivalapp.ui.navigation.ThemeMode
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.festivalapp.data.AppDataContainer
import com.example.festivalapp.ui.theme.FestivalAppTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.festivalapp.data.datastore.UserPreferencesDs
import com.example.festivalapp.ui.navigation.NavigationController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = AppDataContainer(applicationContext)

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

            val themeMode = navigationModel.themeMode.value
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
