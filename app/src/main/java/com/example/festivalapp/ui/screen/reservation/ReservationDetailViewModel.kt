package com.example.festivalapp.ui.screen.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameRepository
import com.example.festivalapp.data.reservation.room.GameWithReservationInfo
import com.example.festivalapp.data.reservation.room.Reservation
import com.example.festivalapp.data.reservation.ReservationRepository
import com.example.festivalapp.data.reservation.retrofit.UpdateReservationRequest
import com.example.festivalapp.data.reservation.room.SuiviReservation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

sealed interface ReservationDetailUiState {
    object Loading : ReservationDetailUiState
    data class Success(
        val reservation: Reservation,
        val games: List<GameWithReservationInfo>,
        val suivis: List<SuiviReservation>
    ) : ReservationDetailUiState
    data class Error(val message: String) : ReservationDetailUiState
}

sealed interface NetworkState {
    object Loading : NetworkState
    object Success : NetworkState
    data class Error(val message: String) : NetworkState
}

class ReservationDetailViewModel(
    private val reservationRepository: ReservationRepository,
    private val gameRepository: GameRepository,
    private val reservationId: Int
): ViewModel() {

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val networkState = _networkState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val availableGames: StateFlow<List<Game>> = reservationRepository.getReservationStream(reservationId)
        .filterNotNull()
        .flatMapLatest { reservation ->
            gameRepository.getGamesStreamByEditor(reservation.idEditor)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val uiState: StateFlow<ReservationDetailUiState> = combine(
        reservationRepository.getReservationStream(reservationId),
        reservationRepository.getGamesStream(reservationId),
        reservationRepository.getSuiviStream(reservationId),
        _networkState
    ) { reservation, games, suivis, network ->
        if (reservation != null) {
            ReservationDetailUiState.Success(reservation, games, suivis)
        } else if (network is NetworkState.Error) {
            ReservationDetailUiState.Error(network.message)
        } else {
            ReservationDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ReservationDetailUiState.Loading
    )

    init {
        refresh()
        viewModelScope.launch {
            reservationRepository.getReservationStream(reservationId)
                .filterNotNull()
                .collect { reservation ->
                    gameRepository.refreshGamesByEditor(reservation.idEditor)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.refreshReservationDetail(reservationId)
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible de rafraîchir les données")
            }
        }
    }

    fun updateStatus(newStatus: String) {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.updateLogistics(reservationId, UpdateReservationRequest(status = newStatus))
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible de modifier le statut")
            }
        }
    }

    fun updateLogistics(nbSmall: Int, nbLarge: Int, nbCity: Int, m2: Int, remise: Double) {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.updateLogistics(reservationId, UpdateReservationRequest(
                    nbSmallTables = nbSmall,
                    nbLargeTables = nbLarge,
                    nbCityHallTables = nbCity,
                    m2 = m2,
                    remise = remise
                ))
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible de modifier la logistique")
            }
        }
    }

    fun updateOrga(typeAnim: Int, listD: Boolean, listR: Boolean, jeuxR: Boolean) {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.updateLogistics(reservationId, UpdateReservationRequest(
                    typeAnimateur = typeAnim,
                    listeDemandee = listD,
                    listeRecue = listR,
                    jeuxRecus = jeuxR
                ))
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible de modifier l'organisation")
            }
        }
    }

    fun addGame(gameId: Int, quantity: Int) {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.addGameToReservation(reservationId, gameId, quantity)
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible d'ajouter un jeu")
            }
        }
    }

    fun removeGame(gameId: Int) {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.removeGameFromReservation(reservationId, gameId)
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible de supprimer le jeu")
            }
        }
    }

    fun updateGameInfo(gameId: Int, quantity: Int, isPlaced: Boolean) {
        viewModelScope.launch {
            _networkState.value = NetworkState.Loading
            try {
                reservationRepository.updateReservationGame(reservationId, gameId, quantity, isPlaced)
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error("Erreur réseau : impossible de modifier les infos du jeu")
            }
        }
    }

    fun clearError() {
        _networkState.value = NetworkState.Success
    }
}