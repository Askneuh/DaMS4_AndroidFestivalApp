package com.example.festivalapp.ui.screen.admin.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.festivalapp.AppViewModelProvider
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserRepository

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AdminUserListScreen(viewModel: AdminUserListViewModel, onLogoutClick: () -> Unit) {
    val networkState by viewModel.networkState.collectAsState()
    val localUsers by viewModel.localUsersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Membres", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Déconnexion", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(localUsers) { user ->
                    UserCard(
                        user = user,
                        onDeleteClick = { viewModel.deleteUser(user) },
                        onUpdateRoleClick = { newRole -> viewModel.updateRole(user.id, newRole) }
                    )
                }
            }

            if (networkState is UserListUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter).padding(8.dp))
            }

            if (networkState is UserListUiState.Error) {
                val errorMsg = (networkState as UserListUiState.Error).message
                Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                    Text(text = errorMsg)
                }
            }
        }
    }
}


@Composable
fun UserCard(
    user: User,
    onDeleteClick: () -> Unit,
    onUpdateRoleClick: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val roleColor = when (user.role.lowercase()) {
        "administrateur" -> Color(0xFFE53935) // Rouge
        "organisateur" -> Color(0xFF8E24AA) // Violet
        "editeur" -> Color(0xFF1E88E5) // Bleu
        else -> Color(0xFF7CB342) // Vert pour Visiteur
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = user.login, style = MaterialTheme.typography.titleLarge)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = roleColor,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = user.role,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Text(text = "ID: ${user.id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Modifier le rôle", tint = Color(0xFF1E88E5))
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer l'utilisateur", tint = Color(0xFFE53935))
                }
            }
        }
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer l'utilisateur ${user.login} ? Cette action est définitive coté serveur.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    showDeleteDialog = false
                }) { Text("Supprimer", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Modifier le rôle de ${user.login}") },
            text = {
                Column {
                    // On propose les 4 rôles possibles
                    val roles = listOf("Administrateur", "Organisateur", "Editeur", "Visiteur")
                    roles.forEach { role ->
                        TextButton(
                            onClick = {
                                onUpdateRoleClick(role)
                                showEditDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(role)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun AdminUserListRoute(onLogoutClick: () -> Unit) {
    val adminViewModel: AdminUserListViewModel = viewModel(factory = AppViewModelProvider.Factory)
    AdminUserListScreen(
        viewModel = adminViewModel,
        onLogoutClick = onLogoutClick
    )
}
