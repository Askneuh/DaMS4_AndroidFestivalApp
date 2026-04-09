package com.example.festivalapp.ui.screen.reservation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameRepository
import com.example.festivalapp.data.reservation.room.GameWithReservationInfo
import com.example.festivalapp.data.reservation.room.Reservation
import com.example.festivalapp.data.reservation.ReservationRepository
import kotlin.math.ceil

data class TariffZoneMock(
    val idTZ: Int,
    val name: String,
    val totalSmallTables: Int,
    val totalLargeTables: Int,
    val totalCityHallTables: Int
)

val mockZone = TariffZoneMock(
    idTZ = 1,
    name = "Zone A",
    totalSmallTables = 50,
    totalLargeTables = 20,
    totalCityHallTables = 10
)

@Composable
fun ReservationDetailScreen(
    viewModel: ReservationDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val availableGames by viewModel.availableGames.collectAsState()

    ReservationDetailScreenContent(
        uiState = uiState,
        allAvailableGames = availableGames,
        onBack = onNavigateBack,
        onRefresh = { viewModel.refresh() },
        onClearError = { viewModel.clearError() },
        onStatusChange = { viewModel.updateStatus(it) },
        onUpdateLogistics = { s, l, c, m, r -> viewModel.updateLogistics(s, l, c, m, r) },
        onUpdateOrga = { anim, listD, listR, jeuxR -> viewModel.updateOrga(anim, listD, listR, jeuxR) },
        onAddGame = { id, qty -> viewModel.addGame(id, qty) },
        onRemoveGame = { id -> viewModel.removeGame(id) },
        onUpdateGame = { id, qty, placed -> viewModel.updateGameInfo(id, qty, placed) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreenContent(
    uiState: ReservationDetailUiState,
    allAvailableGames: List<Game>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onClearError: () -> Unit,
    onStatusChange: (String) -> Unit,
    onUpdateLogistics: (Int, Int, Int, Int, Double) -> Unit,
    onUpdateOrga: (Int, Boolean, Boolean, Boolean) -> Unit,
    onAddGame: (Int, Int) -> Unit,
    onRemoveGame: (Int) -> Unit,
    onUpdateGame: (Int, Int, Boolean) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Statut", "Logistique", "Orga.", "Jeux", "Historique")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails Réservation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is ReservationDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is ReservationDetailUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Erreur : ${uiState.message}", color = MaterialTheme.colorScheme.error)
                            Button(onClick = { onClearError(); onRefresh() }) { Text("Réessayer") }
                            TextButton(onClick = onClearError) { Text("Ignorer") }
                        }
                    }
                    is ReservationDetailUiState.Success -> {
                        when (selectedTabIndex) {
                            0 -> StatusTabContent(uiState.reservation, onStatusChange)
                            1 -> LogisticsTabContent(uiState.reservation, onUpdateLogistics)
                            2 -> OrgaTabContent(uiState.reservation, onUpdateOrga)
                            3 -> GamesTabContent(
                                games = uiState.games,
                                allAvailableGames = allAvailableGames,
                                onUpdateGame = onUpdateGame,
                                onRemoveGame = onRemoveGame,
                                onAddGame = onAddGame
                            )
                            else -> Text("Historique bientôt disponible", Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusTabContent(
    reservation: Reservation,
    onStatusChange: (String) -> Unit
) {
    val statuses = listOf("Pas encore de contact", "Contact pris", "Discussion en cours", "Sera absent", "Considéré absent", "Présent", "Facturé", "Facture payée")
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Modifier le statut", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        statuses.forEach { status ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { onStatusChange(status) }) {
                RadioButton(
                    selected = reservation.status == status,
                    onClick = { onStatusChange(status) }
                )
                Text(status)
            }
        }
    }
}

@Composable
fun LogisticsTabContent(
    reservation: Reservation,
    onUpdateLogistics: (Int, Int, Int, Int, Double) -> Unit
) {
    var smallTables by remember { mutableStateOf(reservation.nbSmallTables.toString()) }
    var largeTables by remember { mutableStateOf(reservation.nbLargeTables.toString()) }
    var cityTables by remember { mutableStateOf(reservation.nbCityHallTables.toString()) }
    var m2 by remember { mutableStateOf(reservation.m2.toString()) }
    var remise by remember { mutableStateOf(reservation.remise.toString()) }

    val sInt = smallTables.toIntOrNull() ?: 0
    val lInt = largeTables.toIntOrNull() ?: 0
    val cInt = cityTables.toIntOrNull() ?: 0
    val m2Int = m2.toIntOrNull() ?: 0
    val remiseDouble = remise.toDoubleOrNull() ?: 0.0

    val smallByM2 = ceil(m2Int.toDouble() / 4.0).toInt()
    val remSmall = mockZone.totalSmallTables - sInt - smallByM2
    val remLarge = mockZone.totalLargeTables - lInt
    val remCity  = mockZone.totalCityHallTables - cInt
    val hasError = remSmall < 0 || remLarge < 0 || remCity < 0

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        OutlinedTextField(
            value = smallTables,
            onValueChange = { smallTables = it },
            label = { Text("Petites tables") },
            isError = remSmall < 0,
            supportingText = { Text("$remSmall restantes") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = largeTables,
            onValueChange = { largeTables = it },
            label = { Text("Grandes tables") },
            isError = remLarge < 0,
            supportingText = { Text("$remLarge restantes") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cityTables,
            onValueChange = { cityTables = it },
            label = { Text("Tables de mairie") },
            isError = remCity < 0,
            supportingText = { Text("$remCity restantes") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = m2,
            onValueChange = { m2 = it },
            label = { Text("Surface (m²)") },
            supportingText = { Text("Utilise $smallByM2 petites tables") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = remise,
            onValueChange = { remise = it },
            label = { Text("Remise (%)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onUpdateLogistics(sInt, lInt, cInt, m2Int, remiseDouble) },
            enabled = !hasError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer Logistique")
        }
    }
}

@Composable
fun OrgaTabContent(
    reservation: Reservation,
    onUpdateOrga: (typeAnim: Int, listD: Boolean, listR: Boolean, jeuxR: Boolean) -> Unit
) {
    var typeAnim by remember { mutableIntStateOf(reservation.typeAnimateur) }
    var listD by remember { mutableStateOf(reservation.listeDemandee) }
    var listR by remember { mutableStateOf(reservation.listeRecue) }
    var jeuxR by remember { mutableStateOf(reservation.jeuxRecus) }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Animation & Suivi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Column {
            Text("Animation", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = typeAnim == 0, onClick = { typeAnim = 0 })
                Text("Besoin de bénévoles")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = typeAnim == 1, onClick = { typeAnim = 1 })
                Text("N'a pas besoin de bénévoles")
            }
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = listD, onCheckedChange = { listD = it })
                Text("Liste demandée")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = listR, onCheckedChange = { listR = it })
                Text("Liste reçue")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = jeuxR, onCheckedChange = { jeuxR = it })
                Text("Jeux reçus")
            }
        }

        Button(
            onClick = { onUpdateOrga(typeAnim, listD, listR, jeuxR) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer Organisation")
        }
    }
}

@Composable
fun GamesTabContent(
    games: List<GameWithReservationInfo>,
    allAvailableGames: List<Game>,
    onUpdateGame: (Int, Int, Boolean) -> Unit,
    onRemoveGame: (Int) -> Unit,
    onAddGame: (Int, Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (games.isEmpty()) {
            Text(
                "Aucun jeu dans cette réservation.",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games) { game ->
                    GameReservationCard(
                        game = game,
                        onUpdate = { qty, placed -> onUpdateGame(game.id, qty, placed) },
                        onRemove = { onRemoveGame(game.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
        }
    }

    if (showAddDialog) {
        GameSelectorDialog(
            availableGames = allAvailableGames,
            onDismiss = { showAddDialog = false },
            onGameSelected = { gameId ->
                onAddGame(gameId, 1)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun GameReservationCard(
    game: GameWithReservationInfo,
    onUpdate: (Int, Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(game.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(game.author ?: "Auteur inconnu", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Retirer", tint = MaterialTheme.colorScheme.error)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quantité
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Qté:", style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = { if (game.quantity > 1) onUpdate(game.quantity - 1, game.isGamePlaced) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Moins")
                    }
                    Text("${game.quantity}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { onUpdate(game.quantity + 1, game.isGamePlaced) }) {
                        Icon(Icons.Default.Add, contentDescription = "Plus")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Placé ?", style = MaterialTheme.typography.bodyMedium)
                    Checkbox(
                        checked = game.isGamePlaced,
                        onCheckedChange = { onUpdate(game.quantity, it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSelectorDialog(
    availableGames: List<Game>,
    onDismiss: () -> Unit,
    onGameSelected: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredGames = remember(searchQuery, availableGames) {
        availableGames.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un jeu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Rechercher un jeu...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(filteredGames) { game ->
                        ListItem(
                            headlineContent = { Text(game.name) },
                            supportingContent = { Text(game.author ?: "") },
                            modifier = Modifier.clickable { onGameSelected(game.id) }
                        )
                    }
                    if (filteredGames.isEmpty()) {
                        item {
                            Text(
                                "Aucun jeu trouvé",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        }
    )
}

@Composable
fun ReservationDetailRoute(
    reservationRepository: ReservationRepository,
    gameRepository: GameRepository,
    reservationId: Int,
    onNavigateBack: () -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ReservationDetailViewModel(
                reservationRepository = reservationRepository,
                gameRepository = gameRepository,
                reservationId = reservationId
            ) as T
        }
    }

    val viewModel: ReservationDetailViewModel = viewModel(
        key = "reservation_$reservationId",
        factory = factory
    )

    ReservationDetailScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}
