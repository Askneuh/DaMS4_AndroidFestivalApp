package com.example.festivalapp.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.AppViewModelProvider
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.ui.theme.FestivalAppTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    // Naviguer vers la page suivante si le login réussit
    LaunchedEffect(loginState) {
        if (loginState is LoginResult.Success) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        loginState = loginState,
        onLogin = { username, password -> viewModel.performLogin(username, password) }
    )
}

@Composable
fun LoginScreenContent(
    loginState: LoginResult?,
    onLogin: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Titre
            Text(
                text = "Connexion",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Champ nom d'utilisateur
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nom d'utilisateur") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = loginState is LoginResult.Error
            )

            // Champ mot de passe
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (username.isNotBlank() && password.isNotBlank()) {
                            onLogin(username, password)
                        }
                    }
                ),
                isError = loginState is LoginResult.Error
            )

            // Message d'erreur
            if (loginState is LoginResult.Error) {
                Text(
                    text = loginState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton de connexion
            Button(
                onClick = { onLogin(username, password) },
                enabled = username.isNotBlank() && password.isNotBlank() && loginState !is LoginResult.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (loginState is LoginResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Se connecter")
                }
            }
        }
    }
}


@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit = {}
) {
    val loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LoginScreen(
        viewModel = loginViewModel,
        onLoginSuccess = onLoginSuccess
    )
}


@Preview(showBackground = true, name = "Default State")
@Composable
fun LoginScreenDefaultPreview() {
    FestivalAppTheme {
        LoginScreenContent(
            loginState = null,
            onLogin = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun LoginScreenErrorPreview() {
    FestivalAppTheme {
        LoginScreenContent(
            loginState = LoginResult.Error("Identifiants incorrects"),
            onLogin = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun LoginScreenLoadingPreview() {
    FestivalAppTheme {
        LoginScreenContent(
            loginState = LoginResult.Loading,
            onLogin = { _, _ -> }
        )
    }
}
