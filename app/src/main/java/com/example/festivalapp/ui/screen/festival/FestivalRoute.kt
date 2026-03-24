package com.example.festivalapp.ui.screen.festival

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.festivalapp.data.APIService

@Composable
fun FestivalRoute(navController: NavController, apiService: APIService) {
    FestivalListScreen(apiService)
}