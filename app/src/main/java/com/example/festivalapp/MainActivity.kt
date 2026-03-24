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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.AppDataContainer
import com.example.festivalapp.data.datastore.UserPreferencesDs
import com.example.festivalapp.ui.screen.login.LoginRoute
import com.example.festivalapp.ui.screen.login.LoginScreen
import com.example.festivalapp.ui.screen.login.LoginScreenContent
import com.example.festivalapp.ui.screen.login.LoginViewModel
import com.example.festivalapp.ui.screen.login.LoginViewModelFactory
import com.example.festivalapp.ui.theme.FestivalAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = AppDataContainer(applicationContext)

        enableEdgeToEdge()
        setContent {
            FestivalAppTheme {
                LoginRoute(
                    authRepository = container.authRepository,
                    onLoginSuccess = {
                        Toast.makeText(this@MainActivity, "Connexion réussie !", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FestivalAppTheme {
        Greeting("Android")
    }
}