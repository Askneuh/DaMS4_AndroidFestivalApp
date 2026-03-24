package com.example.festivalapp.ui.screen.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.ui.theme.FestivalAppTheme

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text=message, color=Color.Red)
    }
}

@Composable
fun UserList(users: List<User>, modifier : Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(8.dp)
    ) {
        items(items=users) {user ->
            Card(
                modifier=modifier.fillMaxWidth().padding(6.dp)
            ) {
                Column(
                    modifier = modifier.padding(12.dp)
                ) {
                    Text(
                        text=user.login,
                        color = Color.Red
                    )
                    HorizontalDivider()
                    Text(text=user.role)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    val sampleUsers = listOf(
        User(id = 1, login = "admin", password = "password", role = "ADMIN"),
        User(id = 2, login = "user1", password = "password", role = "USER"),
        User(id = 3, login = "user2", password = "password", role = "USER")
    )
    FestivalAppTheme {
        UserList(users = sampleUsers, modifier = Modifier)
    }
}