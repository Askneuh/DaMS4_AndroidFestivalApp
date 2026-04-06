package com.example.festivalapp.ui.screen.reservation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameRepository
import com.example.festivalapp.data.reservation.room.GameWithReservationInfo
import com.example.festivalapp.data.reservation.room.Reservation
import com.example.festivalapp.data.reservation.room.ReservationRepository
import com.example.festivalapp.data.reservation.retrofit.UpdateReservationRequest
import com.example.festivalapp.data.reservation.room.SuiviReservation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface ReservationDetailUIState {
    object Loading : ReservationDetailUIState
    data class Success(
        val reservation: Reservation,
        val games: List<GameWithReservationInfo>,
        val suivis: List<SuiviReservation>
    ) : ReservationDetailUIState
    data class Error(val message: String) : ReservationDetailUIState
}

class ReservationDetailViewModel(
    private val reservationRepository: ReservationRepository,
    private val gameRepository: GameRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val reservationId: Int = checkNotNull(savedStateHandle["reservationId"])

    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _availableGames = MutableStateFlow<List<Game>>(emptyList())
    private var lastLoadedEditorId: Int? = null
    val availableGames: StateFlow<List<Game>> = _availableGames
    val uiState: StateFlow<ReservationDetailUIState> = combine(
        reservationRepository.getReservationStream(reservationId),
        reservationRepository.getGamesStream(reservationId),
        reservationRepository.getSuiviStream(reservationId),
        _errorMessage
    ) { reservation, games, suivis, error ->
        if (error != null) {
            ReservationDetailUIState.Error(error)
        } else if (reservation != null) {
            ReservationDetailUIState.Success(reservation, games, suivis)
        } else {
            ReservationDetailUIState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ReservationDetailUIState.Loading
    )

    init {
        refresh()
        viewModelScope.launch {
            reservationRepository.getReservationStream(reservationId)
                .collect { reservation ->
                    reservation?.let { loadGamesForReservation(it.idEditor) }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                reservationRepository.refreshReservationDetail(reservationId)
            } catch (e: Exception) {
                _errorMessage.value = "Erreur de chargement : ${e.message}"
            }
        }
    }

    /** Réinitialise l'erreur (ex. après affichage dans l'UI ou au retry) */
    fun clearError() {
        _errorMessage.value = null
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            try {
                reservationRepository.updateLogistics(reservationId, UpdateReservationRequest(status = newStatus))
            } catch (e: Exception) {
            }
        }
    }

    fun updateLogistics(nbSmall: Int, nbLarge: Int, nbCity: Int, m2: Int, remise: Double) {
        viewModelScope.launch {
            try {
                reservationRepository.updateLogistics(reservationId, UpdateReservationRequest(
                    nbSmallTables = nbSmall,
                    nbLargeTables = nbLarge,
                    nbCityHallTables = nbCity,
                    m2 = m2,
                    remise = remise
                ))
            } catch (e: Exception) {
            }
        }
    }

    fun updateOrga(typeAnim: Int, listD: Boolean, listR: Boolean, jeuxR: Boolean) {
        viewModelScope.launch {
            try {
                reservationRepository.updateLogistics(reservationId, UpdateReservationRequest(
                    typeAnimateur = typeAnim,
                    listeDemandee = listD,
                    listeRecue = listR,
                    jeuxRecus = jeuxR
                ))
            } catch (e: Exception) {
            }
        }
    }



    private fun loadGamesForReservation(idEditor: Int) {
        if (idEditor == lastLoadedEditorId && _availableGames.value.isNotEmpty()) return
        
        viewModelScope.launch {
            try {
                lastLoadedEditorId = idEditor
                val editorGames = gameRepository.getGamesByEditor(idEditor)
                _availableGames.value = editorGames
            } catch (e: Exception) {
                _errorMessage.value = "Erreur chargement jeux"
            }
        }
    }

    fun addGame(gameId: Int, quantity: Int) {
        viewModelScope.launch {
            try {
                reservationRepository.addGameToReservation(reservationId, gameId, quantity)
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de l'ajout du jeu"
                Log.e("VM", "Erreur ajout", e)
            }
        }
    }
    fun removeGame(gameId: Int) {
        viewModelScope.launch {
            try {
                reservationRepository.removeGameFromReservation(reservationId, gameId)
            } catch (e: Exception) { /* Log error */ }
        }
    }
    fun updateGameInfo(gameId: Int, quantity: Int, isPlaced: Boolean) {
        viewModelScope.launch {
            try {
                reservationRepository.updateReservationGame(reservationId, gameId, quantity, isPlaced)
            } catch (e: Exception) { /* Log error */ }
        }
    }


}