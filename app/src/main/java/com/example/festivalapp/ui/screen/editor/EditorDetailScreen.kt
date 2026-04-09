package com.example.festivalapp.ui.screen.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Jeux", "Contacts")

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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // --- TabRow ---
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // --- Loading Indicator ---
            if (networkState is EditorDetailUiState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // --- Tab Content ---
            when (selectedTabIndex) {
                0 -> GamesTabContent(
                    games = games,
                    isLoading = networkState is EditorDetailUiState.Loading,
                    onAddGame = { name, author, minP, maxP, age, dur ->
                        viewModel.addGame(name, author, minP, maxP, age, dur)
                    },
                    onUpdateGame = { id, name, author, minP, maxP, age, dur ->
                        viewModel.updateGame(id, name, author, minP, maxP, age, dur)
                    },
                    onDeleteGame = { id ->
                        viewModel.deleteGame(id)
                    }
                )
                1 -> ContactsTabContent(
                    contacts = contacts,
                    isLoading = networkState is EditorDetailUiState.Loading,
                    onAddContact = { name, email, phone, role, priority ->
                        viewModel.addContact(name, email, phone, role, priority)
                    },
                    onUpdateContact = { id, name, email, phone, role, priority ->
                        viewModel.updateContact(id, name, email, phone, role, priority)
                    },
                    onDeleteContact = { id ->
                        viewModel.deleteContact(id)
                    }
                )
            }
        }
    }
}

@Composable
fun GamesTabContent(
    games: List<Game>,
    isLoading: Boolean,
    onAddGame: (name: String, author: String, nbMinPlayer: Int, nbMaxPlayer: Int, minimumAge: Int, duration: Int) -> Unit,
    onUpdateGame: (id: Int, name: String, author: String, nbMinPlayer: Int, nbMaxPlayer: Int, minimumAge: Int, duration: Int) -> Unit,
    onDeleteGame: (id: Int) -> Unit
) {
    var showGameDialog by remember { mutableStateOf(false) }
    var gameToEdit by remember { mutableStateOf<Game?>(null) }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<Game?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (games.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucun jeu enregistré pour cet éditeur.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(games, key = { it.id }) { game ->
                    GameCard(
                        game = game,
                        onEdit = { 
                            gameToEdit = game
                            showGameDialog = true 
                        },
                        onDelete = {
                            gameToDelete = game
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                gameToEdit = null
                showGameDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
        }
    }

    if (showGameDialog) {
        GameDialog(
            initialGame = gameToEdit,
            onDismiss = { 
                showGameDialog = false 
                gameToEdit = null
            },
            onConfirm = { name, author, minP, maxP, age, dur ->
                if (gameToEdit == null) {
                    onAddGame(name, author, minP, maxP, age, dur)
                } else {
                    onUpdateGame(gameToEdit!!.id, name, author, minP, maxP, age, dur)
                }
                showGameDialog = false
                gameToEdit = null
            }
        )
    }

    if (showDeleteDialog && gameToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false 
                gameToDelete = null
            },
            title = { Text("Supprimer un jeu") },
            text = { Text("Voulez-vous vraiment supprimer le jeu \"${gameToDelete?.name}\" ?") },
            confirmButton = {
                Button(
                    onClick = {
                        gameToDelete?.let { onDeleteGame(it.id) }
                        showDeleteDialog = false
                        gameToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false 
                    gameToDelete = null
                }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun GameDialog(
    initialGame: Game?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, author: String, nbMinPlayer: Int, nbMaxPlayer: Int, minimumAge: Int, duration: Int) -> Unit
) {
    var name by remember { mutableStateOf(initialGame?.name ?: "") }
    var author by remember { mutableStateOf(initialGame?.author ?: "") }
    var nbMinPlayer by remember { mutableStateOf(initialGame?.nbMinPlayer?.toString() ?: "1") }
    var nbMaxPlayer by remember { mutableStateOf(initialGame?.nbMaxPlayer?.toString() ?: "4") }
    var minimumAge by remember { mutableStateOf(initialGame?.minimumAge?.toString() ?: "6") }
    var duration by remember { mutableStateOf(initialGame?.duration?.toString() ?: "30") }

    val isEditing = initialGame != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Modifier un jeu" else "Ajouter un jeu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du jeu *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Auteur *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nbMinPlayer,
                        onValueChange = { nbMinPlayer = it },
                        label = { Text("Min joueurs") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = nbMaxPlayer,
                        onValueChange = { nbMaxPlayer = it },
                        label = { Text("Max joueurs") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minimumAge,
                        onValueChange = { minimumAge = it },
                        label = { Text("Âge min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Durée (min)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        name,
                        author,
                        nbMinPlayer.toIntOrNull() ?: 1,
                        nbMaxPlayer.toIntOrNull() ?: 4,
                        minimumAge.toIntOrNull() ?: 6,
                        duration.toIntOrNull() ?: 30
                    )
                },
                enabled = name.isNotBlank() && author.isNotBlank()
            ) {
                Text(if (isEditing) "Modifier" else "Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun ContactsTabContent(
    contacts: List<Contact>,
    isLoading: Boolean,
    onAddContact: (name: String, email: String, phone: String?, role: String?, priority: Boolean) -> Unit,
    onUpdateContact: (id: Int, name: String, email: String, phone: String?, role: String?, priority: Boolean) -> Unit,
    onDeleteContact: (id: Int) -> Unit
) {
    var showContactDialog by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (contacts.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucun contact enregistré pour cet éditeur.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(contacts, key = { it.id }) { contact ->
                    ContactCard(
                        contact = contact,
                        onEdit = { 
                            contactToEdit = contact
                            showContactDialog = true 
                        },
                        onDelete = {
                            contactToDelete = contact
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                contactToEdit = null
                showContactDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter un contact")
        }
    }

    if (showContactDialog) {
        ContactDialog(
            initialContact = contactToEdit,
            onDismiss = { 
                showContactDialog = false 
                contactToEdit = null
            },
            onConfirm = { name, email, phone, role, priority ->
                if (contactToEdit == null) {
                    onAddContact(name, email, phone, role, priority)
                } else {
                    onUpdateContact(contactToEdit!!.id, name, email, phone, role, priority)
                }
                showContactDialog = false
                contactToEdit = null
            }
        )
    }

    if (showDeleteDialog && contactToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false 
                contactToDelete = null
            },
            title = { Text("Supprimer un contact") },
            text = { Text("Voulez-vous vraiment supprimer le contact \"${contactToDelete?.name}\" ?") },
            confirmButton = {
                Button(
                    onClick = {
                        contactToDelete?.let { onDeleteContact(it.id) }
                        showDeleteDialog = false
                        contactToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false 
                    contactToDelete = null
                }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun ContactDialog(
    initialContact: Contact?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String?, role: String?, priority: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var email by remember { mutableStateOf(initialContact?.email ?: "") }
    var phone by remember { mutableStateOf(initialContact?.phone ?: "") }
    var role by remember { mutableStateOf(initialContact?.role ?: "") }
    var priority by remember { mutableStateOf(initialContact?.priority ?: false) }

    val isEditing = initialContact != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Modifier un contact" else "Ajouter un contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Rôle") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = priority, onCheckedChange = { priority = it })
                    Text("Contact prioritaire")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, email, phone, role, priority) },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text(if (isEditing) "Modifier" else "Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun GameCard(
    game: Game,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = game.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "Auteur: ${game.author}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Joueurs: ${game.nbMinPlayer}-${game.nbMaxPlayer}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Age: ${game.minimumAge}+", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Durée: ${game.duration} min", style = MaterialTheme.typography.bodySmall)
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
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
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
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
