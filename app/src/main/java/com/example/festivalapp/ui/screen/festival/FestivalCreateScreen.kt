package com.example.festivalapp.ui.screen.festival

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalCreateScreen(
    festivalId: String? = null,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (festivalId == null) "Créer un festival" else "Éditer un festival") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Icon Back
                        Text("<")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Festival Name, Zones...")
            // TODO: Name field, Zones configuration
        }
    }
}
