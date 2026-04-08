package com.example.festivalapp.ui.screen.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.festivalapp.data.festival.FestivalRepository

class FestivalViewModelFactory(
    private val festivalRepository: FestivalRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FestivalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FestivalViewModel(festivalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}