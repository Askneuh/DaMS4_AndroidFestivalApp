package com.example.festivalapp.ui.screen.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    editorId: Int,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réservation Éditeur") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("<")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Workflow de réservation pour l'id: $editorId", modifier = Modifier.padding(16.dp))
            // TODO: Contact dates, workflow status (Pas encore contacté, Discussion en cours...), games allocation, billing info...
        }
    }
}
