package com.example.festivalapp.ui.screen.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FestivalGamesListViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val games: StateFlow<List<Game>> = combine(
        gameRepository.getAllGamesStream(),
        searchQuery
    ) { gamesList, query ->
        if (query.isBlank()) {
            gamesList
        } else {
            gamesList.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            try {
                gameRepository.refreshGames()
            } catch (e: Exception) {
                // Ignore errors for now
            }
        }
    }
}

@Composable
fun FestivalGamesListRoute(
    gameRepository: GameRepository,
    onMenuClick: () -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FestivalGamesListViewModel(gameRepository) as T
        }
    }
    val viewModel: FestivalGamesListViewModel = viewModel(factory = factory)
    FestivalGamesListScreen(
        viewModel = viewModel,
        onMenuClick = onMenuClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalGamesListScreen(
    viewModel: FestivalGamesListViewModel,
    onMenuClick: () -> Unit
) {
    val games by viewModel.games.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jeux du Festival") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Rechercher un jeu...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (games.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Aucun jeu trouvé.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(games, key = { it.id }) { game ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(game.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Auteur: ${game.author ?: "Inconnu"}", style = MaterialTheme.typography.bodyMedium)
                                Text("Durée: ${game.duration} | Min Age: ${game.minimumAge}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
