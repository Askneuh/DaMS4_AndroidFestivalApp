package com.example.festivalapp.ui.screen.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalGamesListScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jeux du Festival") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        // Icon Menu
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Liste des jeux associés aux réservations du festival courant", modifier = Modifier.padding(16.dp))
            // TODO: List of games with their assigned plan zones and publisher info.
        }
    }
}
