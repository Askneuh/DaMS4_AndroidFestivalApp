package com.example.festivalapp.ui.screen.festival

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.festival.PlanZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalListScreen(
    viewModel: FestivalViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToFestivalDetail: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadCurrentFestival()
        viewModel.loadAllFestivals()
    }
    
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
            FloatingActionButton(
                onClick = { viewModel.openForm() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Créer un festival")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.successMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))
                ) {
                    Text(
                        text = uiState.successMessage!!,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF155724)
                    )
                }
            }
            
            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8D7DA))
                ) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF721C24)
                    )
                }
            }
            
            if (uiState.isLoading && uiState.festivals.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.festivals.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Aucun festival disponible")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.festivals) { festival ->
                        FestivalCard(
                            festival = festival,
                            isCurrent = festival.name == uiState.currentFestival?.name,
                            onEdit = { viewModel.openForm(festival) },
                            onDelete = { viewModel.deleteFestival(festival.name) },
                            onMakeCurrent = { viewModel.setCurrentFestival(festival.name) },
                            onClick = { onNavigateToFestivalDetail(festival.name) }
                        )
                    }
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
fun FestivalCard(
    festival: Festival,
    isCurrent: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMakeCurrent: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isCurrent) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = festival.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (isCurrent) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Courant", fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(text = "Tables", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(text = "${festival.nbSmallTables}p / ${festival.nbLargeTables}g / ${festival.nbCityHallTables}m", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            if (festival.tariffZones.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(text = "Zones Tarifaires", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    festival.tariffZones.forEach { zone ->
                        AssistChip(
                            onClick = {},
                            label = { Text(zone.name) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }

            if (festival.planZones.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(text = "Zones Plan (Géo)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    festival.planZones.forEach { pZone ->
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, null, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("${pZone.name} (${pZone.nbTables})")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isCurrent) {
                    TextButton(onClick = onMakeCurrent) {
                        Text("Définir courant")
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Éditer", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}