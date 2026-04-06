package com.example.festivalapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.festivalapp.data.festival.FestivalRepository
import com.example.festivalapp.ui.screen.festival.FestivalRoute

@Composable
fun AppNavigation(
    navController: NavHostController,
    festivalRepository: FestivalRepository
) {
    NavHost(navController, startDestination = "festivals") {
        composable("festivals") {
            FestivalRoute(navController, festivalRepository)
        }
    }
}