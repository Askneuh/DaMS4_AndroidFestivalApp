package com.example.festivalapp.ui.screen.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationOverviewScreen(
    onNavigateToReservationDetail: (Int) -> Unit,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suivi Festival") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        // Icon Menu
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Liste des éditeurs + réservations pour le festival courant", modifier = Modifier.padding(16.dp))
            // TODO: Publisher lists with reservation status, contact info, total tables...
        }
    }
}
