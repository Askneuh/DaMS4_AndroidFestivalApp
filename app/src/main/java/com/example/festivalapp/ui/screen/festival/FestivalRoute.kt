package com.example.festivalapp.ui.screen.festival

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.festivalapp.data.festival.FestivalRepository

@Composable
fun FestivalRoute(
    navController: NavController,
    festivalRepository: FestivalRepository
) {
    val factory = FestivalViewModelFactory(festivalRepository)
    val viewModel: FestivalViewModel = viewModel(factory = factory)
    
    FestivalListScreen(viewModel = viewModel)
}