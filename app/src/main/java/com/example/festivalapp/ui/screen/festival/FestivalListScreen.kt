package com.example.festivalapp.ui.screen.festival

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.festival.PlanZone
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalListScreen(
    viewModel: FestivalViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToFestivalDetail: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val festivals = uiState.festivals
    val isLoading = uiState.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festivals") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Créer un festival")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(festivals) { festival ->
                    FestivalItem(
                        festival = festival,
                        onClick = { onNavigateToFestivalDetail(festival.name) }
                    )
                }
            }
        }
    }
    
    if (uiState.showForm) {
        FestivalFormDialog(
            festival = uiState.selectedFestival,
            isEditMode = uiState.isEditMode,
            onDismiss = { viewModel.closeForm() },
            onSave = { festival ->
                if (uiState.isEditMode && uiState.selectedFestival != null) {
                    viewModel.updateFestival(uiState.selectedFestival!!.name, festival)
                } else {
                    viewModel.createFestival(festival)
                }
            },
            onCalculateRemaining = { viewModel.calculateRemainingTables(it) },
            onPrepareFestival = { name, tZones, pZones, begin, end ->
                viewModel.prepareFestivalForSave(name, tZones, pZones, begin, end)
            }

        )
    }
}

@Composable
fun FestivalItem(
    festival: Festival,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(festival.name, style = MaterialTheme.typography.titleLarge)
            Text(
                "Du ${festival.beginDate ?: "?"} au ${festival.endDate ?: "?"}",
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                "Tables: ${festival.nbSmallTables}p, ${festival.nbLargeTables}g, ${festival.nbCityHallTables}m",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}