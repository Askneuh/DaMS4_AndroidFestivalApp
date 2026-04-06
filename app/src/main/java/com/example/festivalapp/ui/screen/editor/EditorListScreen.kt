package com.example.festivalapp.ui.screen.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.example.festivalapp.data.editor.room.EditorRepository
import com.example.festivalapp.data.editor.room.Editor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorListScreen(
    viewModel: EditorListViewModel,
    onEditorClick: (Int) -> Unit
) {
    val networkState by viewModel.networkState.collectAsState()
    val editors by viewModel.filteredItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display network errors in Snackbar
    LaunchedEffect(networkState) {
        if (networkState is EditorListUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (networkState as EditorListUiState.Error).message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Éditeurs", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- Search Bar ---
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Rechercher un éditeur...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true
                )
            }

            // --- Loading Indicator ---
            if (networkState is EditorListUiState.Loading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                }
            }

            // --- List of Editors ---
            if (editors.isEmpty() && networkState !is EditorListUiState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucun éditeur trouvé.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(editors, key = { it.id }) { editor ->
                    EditorCard(editor = editor, onClick = { onEditorClick(editor.id) })
                }
            }
        }
    }
}

@Composable
fun EditorCard(editor: Editor, onClick: () -> Unit) {
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
                Text(
                    text = editor.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (editor.exposant) {
                        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                            Text("Exposant", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    if (editor.distributeur) {
                        Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                            Text("Distributeur", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            Button(onClick = onClick) {
                Text("Détails")
            }
        }
    }
}

@Composable
fun EditorListRoute(
    editorRepository: EditorRepository,
    onEditorClick: (Int) -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EditorListViewModel(editorRepository) as T
        }
    }

    val viewModel: EditorListViewModel = viewModel(factory = factory)

    EditorListScreen(
        viewModel = viewModel,
        onEditorClick = onEditorClick
    )
}
