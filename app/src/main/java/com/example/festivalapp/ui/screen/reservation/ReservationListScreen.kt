package com.example.festivalapp.ui.screen.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.reservation.room.EditorWithReservationTuple
import com.example.festivalapp.data.reservation.ReservationRepository

fun getStatusColor(status: String?): Color = when (status) {
    "Présent"              -> Color(0xFF4CAF50)
    "Facturé"              -> Color(0xFF2196F3)
    "Facture payée"        -> Color(0xFF388E3C)
    "Contact pris"         -> Color(0xFFFFC107)
    "Discussion en cours"  -> Color(0xFFFF9800)
    "Sera absent"          -> Color(0xFFF44336)
    "Considéré absent"     -> Color(0xFFF44336)
    else                   -> Color(0xFF9E9E9E)
}

val availableStatuses = listOf(
    "all", "Pas encore de contact", "Contact pris", "Discussion en cours",
    "Sera absent", "Considéré absent", "Présent", "Facturé", "Facture payée"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(
    viewModel: ReservationListViewModel,
    onLogoutClick: () -> Unit,
    onReservationClick: (Int) -> Unit
) {
    val festivalName by viewModel.currentFestivalName.collectAsState()
    val networkState by viewModel.networkState.collectAsState()
    val items by viewModel.filteredItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(networkState) {
        if (networkState is ReservationListUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (networkState as ReservationListUiState.Error).message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Réservations", fontWeight = FontWeight.Bold)
                        Text(
                            text = festivalName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Déconnexion")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Rechercher un éditeur...") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { statusMenuExpanded = true }) {
                        Text(if (statusFilter == "all") "Tous les statuts" else statusFilter)
                    }
                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        availableStatuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(if (status == "all") "Tous les statuts" else status) },
                                onClick = {
                                    viewModel.statusFilter.value = status
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (networkState is ReservationListUiState.Loading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                }
            }

            if (items.isEmpty() && networkState !is ReservationListUiState.Loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Text("Aucun éditeur trouvé.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(items, key = { it.editorId }) { item ->
                    EditorReservationCard(
                        item = item,
                        onReservationClick = onReservationClick,
                        onCreateReservation = { viewModel.createReservation(item.editorId) }
                    )
                }
            }
        }
    }
}

@Composable
fun EditorReservationCard(
    item: EditorWithReservationTuple,
    onReservationClick: (Int) -> Unit,
    onCreateReservation: () -> Unit
) {
    val statusColor = getStatusColor(item.status)
    val displayStatus = item.status ?: "Pas encore de contact"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.editorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = statusColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = displayStatus,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (item.totalTables != null && item.totalTables > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tables : ${item.totalTables}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            if (item.idReservation != null) {
                Button(onClick = { onReservationClick(item.idReservation) }) {
                    Text("Gérer #${item.idReservation}")
                }
            } else {
                OutlinedButton(onClick = onCreateReservation) {
                    Text("Réserver")
                }
            }
        }
    }
}

@Composable
fun ReservationListRoute(
    reservationRepository: ReservationRepository,
    onLogoutClick: () -> Unit,
    onReservationClick: (Int) -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ReservationListViewModel(reservationRepository) as T
        }
    }

    val viewModel: ReservationListViewModel = viewModel(factory = factory)

    ReservationListScreen(
        viewModel = viewModel,
        onLogoutClick = onLogoutClick,
        onReservationClick = onReservationClick
    )
}
