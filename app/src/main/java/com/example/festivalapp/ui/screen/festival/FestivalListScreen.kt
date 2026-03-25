package com.example.festivalapp.ui.screen.festival

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.festival.Festival
import com.example.festivalapp.data.APIService
import kotlinx.coroutines.launch

@Composable
fun FestivalListScreen(
    apiService: APIService,
    viewModel: FestivalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        loadFestivals(viewModel, apiService, scope)
    }
    
    Scaffold(
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Liste des Festivals",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            if (uiState.successMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
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
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
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
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp)
                )
            } else if (uiState.festivals.isEmpty()) {
                Text(
                    text = "Aucun festival disponible",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.festivals) { festival ->
                        FestivalCard(
                            festival = festival,
                            isCurrent = festival.name == uiState.currentFestival?.name,
                            onEdit = { viewModel.openForm(festival) },
                            onDelete = { 
                                scope.launch { 
                                    deleteFestival(festival.name, viewModel, apiService)
                                }
                            },
                            onMakeCurrent = { 
                                scope.launch { 
                                    setCurrentFestival(festival.name, viewModel, apiService)
                                }
                            }
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
                scope.launch {
                    if (uiState.isEditMode && uiState.selectedFestival != null) {
                        updateFestival(uiState.selectedFestival!!.name, festival, viewModel, apiService)
                    } else {
                        createFestival(festival, viewModel, apiService)
                    }
                }
            },
            onCalculateRemaining = { viewModel.calculateRemainingTables(it) },
            onPrepareFestival = { name, zones, begin, end ->
                viewModel.prepareFestivalForSave(name, zones, begin, end)
            }
        )
    }
}

// ========== API CALLS IN COMPOSABLE ==========

private suspend fun loadFestivals(
    viewModel: FestivalViewModel,
    apiService: APIService,
    scope: kotlinx.coroutines.CoroutineScope
) {
    viewModel.setLoading(true)
    try {
        val festivals = apiService.getAllFestivals()
        viewModel.setFestivals(festivals)
        
        try {
            val current = apiService.getCurrentFestival()
            viewModel.setCurrentFestival(current)
        } catch (e: Exception) {
            viewModel.setCurrentFestival(null)
        }
        viewModel.setLoading(false)
    } catch (e: Exception) {
        viewModel.setError(e.message ?: "Erreur lors du chargement")
        viewModel.setLoading(false)
    }
}

private suspend fun createFestival(
    festival: Festival,
    viewModel: FestivalViewModel,
    apiService: APIService
) {
    viewModel.setLoading(true)
    println("🔄 Création festival: ${festival.name}")
    try {
        val newFestival = apiService.createFestival(festival)
        println("✅ Festival créé: ${newFestival.name}")
        viewModel.setSuccessMessage("Festival créé avec succès!")
        viewModel.closeForm()
        viewModel.setLoading(false)
        loadFestivals(viewModel, apiService, kotlinx.coroutines.GlobalScope)
    } catch (e: Exception) {
        println("❌ Erreur: ${e.message}")
        println("❌ Stack trace: ${e.stackTraceToString()}")
        viewModel.setError(e.message ?: "Erreur lors de la création")
        viewModel.setLoading(false)
    }
}

private suspend fun updateFestival(
    originalName: String,
    festival: Festival,
    viewModel: FestivalViewModel,
    apiService: APIService
) {
    viewModel.setLoading(true)
    try {
        val updatedFestival = apiService.updateFestivalByName(originalName, festival)
        viewModel.setSuccessMessage("Festival mis à jour avec succès!")
        viewModel.closeForm()
        viewModel.setLoading(false)
        loadFestivals(viewModel, apiService, kotlinx.coroutines.GlobalScope)
    } catch (e: Exception) {
        viewModel.setError(e.message ?: "Erreur lors de la modification")
        viewModel.setLoading(false)
    }
}

private suspend fun deleteFestival(
    name: String,
    viewModel: FestivalViewModel,
    apiService: APIService
) {
    viewModel.setLoading(true)
    try {
        apiService.deleteFestival(name)
        viewModel.setSuccessMessage("Festival supprimé avec succès!")
        viewModel.setLoading(false)
        loadFestivals(viewModel, apiService, kotlinx.coroutines.GlobalScope)
    } catch (e: Exception) {
        viewModel.setError(e.message ?: "Erreur lors de la suppression")
        viewModel.setLoading(false)
    }
}

private suspend fun setCurrentFestival(
    name: String,
    viewModel: FestivalViewModel,
    apiService: APIService
) {
    viewModel.setLoading(true)
    try {
        val festival = apiService.setCurrentFestival(name)
        viewModel.setSuccessMessage("Le festival \"$name\" est désormais le festival courant!")
        viewModel.setCurrentFestival(festival)
        viewModel.setLoading(false)
        loadFestivals(viewModel, apiService, kotlinx.coroutines.GlobalScope)
    } catch (e: Exception) {
        viewModel.setError(e.message ?: "Erreur")
        viewModel.setLoading(false)
    }
}

@Composable
fun FestivalCard(
    festival: Festival,
    isCurrent: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMakeCurrent: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isCurrent) Modifier.border(2.dp, MaterialTheme.colorScheme.primary) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = festival.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (isCurrent) {
                AssistChip(
                    onClick = {},
                    label = { Text("Festival courant", fontSize = 10.sp) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text(text = "Petites tables: ${festival.nbSmallTables}", fontSize = 12.sp)
            Text(text = "Grandes tables: ${festival.nbLargeTables}", fontSize = 12.sp)
            Text(text = "Tables mairie: ${festival.nbCityHallTables}", fontSize = 12.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
            if (!isCurrent) {
                Button(onClick = onMakeCurrent, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Courant")
                }
            }
        }
    }
}