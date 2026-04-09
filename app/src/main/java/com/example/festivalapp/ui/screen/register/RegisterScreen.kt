package com.example.festivalapp.ui.screen.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.AppViewModelProvider
import com.example.festivalapp.ui.screen.login.LoginScreen
import com.example.festivalapp.ui.screen.login.LoginScreenContent
import com.example.festivalapp.ui.screen.login.LoginViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit
) {
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is RegisterUiState.Success) {
            onRegisterSuccess()
        }
    }

    RegisterScreenContent(
        registerState = registerState,
        onRegister = { username: String, password: String -> viewModel.performRegister(username, password) }
    )
}

@Composable
fun RegisterScreenContent(
    registerState: RegisterUiState?,
    onRegister: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember {mutableStateOf("")}
    var passwordVisible by remember { mutableStateOf(false) }
    var password2Visible by remember { mutableStateOf(false) }

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
            Text(
                text = "Inscription",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it},
                label = { Text("Nom d'utilisateur")},
                leadingIcon = {Icon(Icons.Default.Person, contentDescription = null)},
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = registerState is RegisterUiState.Error
            )

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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = registerState is RegisterUiState.Error
            )

            OutlinedTextField(
                value = password2,
                onValueChange = { password2 = it },
                label = { Text("Vérifier le mot de passe") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { password2Visible = !password2Visible }) {
                        Icon(
                            imageVector = if (password2Visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (password2Visible) "Masquer" else "Afficher"
                        )
                    }
                },
                visualTransformation = if (password2Visible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = registerState is RegisterUiState.Error
            )

            if (registerState is RegisterUiState.Error) {
                Text(
                    text = registerState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val isFormFilled = username.isNotBlank() && password.isNotBlank() && password2.isNotBlank()
            val passwordsMatch = password == password2
            if (!isFormFilled) {
                Text(
                    text = "Veuillez remplir tous les champs.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (!passwordsMatch) {
                Text(
                    text = "Les mots de passe ne correspondent pas.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { onRegister(username, password) },
                enabled = username.isNotBlank() && password.isNotBlank() && password2.isNotBlank() && registerState !is RegisterUiState.Loading && password == password2,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (registerState is RegisterUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("S'enregistrer")
                }
            }
        }
    }
}

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit = {}
) {
    val registerViewModel: RegisterViewModel = viewModel(factory = AppViewModelProvider.Factory)

    RegisterScreen(
        viewModel = registerViewModel,
        onRegisterSuccess = onRegisterSuccess
    )
}