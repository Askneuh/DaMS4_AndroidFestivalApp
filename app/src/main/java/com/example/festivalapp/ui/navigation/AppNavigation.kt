package com.example.festivalapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.festivalapp.data.APIService
import com.example.festivalapp.ui.screen.festival.FestivalRoute

@Composable
fun AppNavigation(navController: NavHostController, apiService: APIService) {
    NavHost(navController, startDestination = "festivals") {
        composable("festivals") {
            FestivalRoute(navController, apiService)
        }
    }
}