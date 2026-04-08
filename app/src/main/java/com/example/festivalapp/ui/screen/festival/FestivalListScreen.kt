package com.example.festivalapp.ui.screen.festival

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToFestivalDetail: (Int) -> Unit,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festivals") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        // Icon would be Menu
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                // Icon would be Add
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Liste des festivals", modifier = Modifier.padding(16.dp))
            // TODO: List of festival cards
        }
    }
}
