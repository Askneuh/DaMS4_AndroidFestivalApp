package com.example.festivalapp

import android.os.Bundle
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.AppDataContainer
import com.example.festivalapp.ui.screen.login.LoginRoute
import com.example.festivalapp.ui.screen.login.LoginScreen
import com.example.festivalapp.ui.screen.login.LoginScreenContent
import com.example.festivalapp.ui.screen.login.LoginViewModel
import com.example.festivalapp.ui.screen.login.LoginViewModelFactory
import com.example.festivalapp.ui.theme.FestivalAppTheme
import java.lang.reflect.Modifier


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as FestivalApplication
        val container = AppDataContainer(applicationContext)


        enableEdgeToEdge()
        setContent {
            FestivalAppTheme {
                LoginRoute(
                    authRepository = app.authRepository,
                    onLoginSuccess = {
                        Toast.makeText(this@MainActivity, "Connexion réussie !", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }

}
