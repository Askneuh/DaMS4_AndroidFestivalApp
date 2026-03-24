package com.example.festivalapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.festivalapp.data.AppDataContainer
import com.example.festivalapp.ui.navigation.AppNavigation
import com.example.festivalapp.ui.screen.login.LoginRoute
import com.example.festivalapp.ui.theme.FestivalAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = AppDataContainer(applicationContext)

        enableEdgeToEdge()
        setContent {
            FestivalAppTheme {
                val navController = rememberNavController()
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    AppNavigation(navController, container.apiService)
                } else {
                    LoginRoute(
                        authRepository = container.authRepository,
                        onLoginSuccess = {
                            Toast.makeText(
                                this@MainActivity,
                                "Connexion réussie !",
                                Toast.LENGTH_SHORT
                            ).show()
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}