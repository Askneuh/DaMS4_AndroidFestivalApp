package com.example.festivalapp.ui.screen.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.data.contact.room.Contact
import com.example.festivalapp.data.editor.room.EditorRepository
import com.example.festivalapp.data.game.room.Game

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorDetailScreen(
    viewModel: EditorDetailViewModel,
    onBackClick: () -> Unit
) {
    val editor by viewModel.editor.collectAsState()
    val games by viewModel.games.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val networkState by viewModel.networkState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(networkState) {
        if (networkState is EditorDetailUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (networkState as EditorDetailUiState.Error).message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(editor?.name ?: "Détails Éditeur") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (networkState is EditorDetailUiState.Loading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            // --- Section Jeux ---
            item {
                Text(
                    text = "Jeux",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (games.isEmpty() && networkState !is EditorDetailUiState.Loading) {
                item {
                    Text("Aucun jeu enregistré pour cet éditeur.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(games, key = { it.id }) { game ->
                    GameCard(game = game)
                }
            }

            // --- Section Contacts ---
            item {
                Text(
                    text = "Contacts",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (contacts.isEmpty() && networkState !is EditorDetailUiState.Loading) {
                item {
                    Text("Aucun contact enregistré pour cet éditeur.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(contacts, key = { it.id }) { contact ->
                    ContactCard(contact = contact)
                }
            }
        }
    }
}

@Composable
fun GameCard(game: Game) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = game.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = "Auteur: ${game.author}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Joueurs: ${game.nbMinPlayer}-${game.nbMaxPlayer}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Age: ${game.minimumAge}+", style = MaterialTheme.typography.bodySmall)
                Text(text = "Durée: ${game.duration} min", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = contact.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            if (!contact.role.isNullOrBlank()) {
                Text(text = contact.role ?: "", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Email: ${contact.email}", style = MaterialTheme.typography.bodySmall)
            if (!contact.phone.isNullOrBlank()) {
                Text(text = "Tél: ${contact.phone}", style = MaterialTheme.typography.bodySmall)
            }
            if (contact.priority) {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text("Prioritaire", modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}

@Composable
fun EditorDetailRoute(
    editorRepository: EditorRepository,
    editorId: Int,
    onBackClick: () -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EditorDetailViewModel(editorRepository, editorId) as T
        }
    }

    val viewModel: EditorDetailViewModel = viewModel(
        key = "editor_$editorId",
        factory = factory
    )

    EditorDetailScreen(
        viewModel = viewModel,
        onBackClick = onBackClick
    )
}
