package com.example.festivalapp.data.reservation

import com.example.festivalapp.data.editor.room.EditorDAO
import com.example.festivalapp.data.reservation.retrofit.AddGameRequest
import com.example.festivalapp.data.reservation.retrofit.UpdateGameRequest
import com.example.festivalapp.data.reservation.retrofit.UpdateReservationRequest
import com.example.festivalapp.data.reservation.room.EditorWithReservationTuple
import com.example.festivalapp.data.reservation.retrofit.reservation.*
import com.example.festivalapp.data.reservation.retrofit.suivi.*
import com.example.festivalapp.data.reservation.room.GameWithReservationInfo
import com.example.festivalapp.data.reservation.room.Reservation
import com.example.festivalapp.data.reservation.room.ReservationDAO
import com.example.festivalapp.data.reservation.room.ReservationGame
import com.example.festivalapp.data.reservation.room.ReservationGameDAO
import com.example.festivalapp.data.reservation.room.SuiviReservation
import com.example.festivalapp.data.reservation.room.SuiviReservationDAO
import kotlinx.coroutines.flow.Flow

class ReservationRepository(
    private val reservationDAO: ReservationDAO,
    private val editorDAO: EditorDAO,
    private val suiviDAO: SuiviReservationDAO,
    private val reservationGameDAO: ReservationGameDAO,
    private val reservationApi: ReservationApiService,
    private val suiviApi: SuiviApiService
) {

    fun getEditorsWithReservations(festivalName: String): Flow<List<EditorWithReservationTuple>> {
        return reservationDAO.getEditorsWithReservationsStatus(festivalName)
    }

    fun getReservationStream(id: Int): Flow<Reservation?> =
        reservationDAO.getReservationById(id)

    fun getGamesStream(id: Int): Flow<List<GameWithReservationInfo>> =
        reservationGameDAO.getGamesForReservation(id)

    fun getSuiviStream(id: Int): Flow<List<SuiviReservation>> =
        suiviDAO.getSuivisByReservation(id)

    suspend fun refreshFromNetwork(festivalName: String) {
        val dtoList = reservationApi.getEditorsWithReservations(festivalName)
        val pairs = dtoList.map { it.toRoomEntities(festivalName) }
        editorDAO.insertAll(pairs.map { it.first })
        reservationDAO.clearForFestival(festivalName)
        val reservations = pairs.mapNotNull { it.second }
        reservationDAO.insertAll(reservations)
    }

    suspend fun createReservation(editorId: Int, festivalName: String) {
        reservationApi.createReservation(CreateReservationRequest(idEditor = editorId, festivalName = festivalName))
        refreshFromNetwork(festivalName)
    }

    suspend fun refreshReservationDetail(reservationId: Int) {
        val reservationDto = reservationApi.getReservationById(reservationId)
        reservationDAO.insert(reservationDto.toEntity())

        val gamesDtoList = reservationApi.getReservationGames(reservationId)
        reservationGameDAO.replaceAllGamesForReservation(
            reservationId,
            gamesDtoList.map { it.toLinkEntity() }
        )

        val suiviDtoList = suiviApi.getByReservation(reservationId)
        suiviDAO.insertAll(suiviDtoList.map { it.toEntity() })
    }


    suspend fun updateLogistics(reservationId: Int, request: UpdateReservationRequest) {
        reservationApi.updateReservation(reservationId, request)
        refreshReservationDetail(reservationId)
    }

    suspend fun addGameToReservation(reservationId: Int, gameId: Int, quantity: Int) {
        // Appelle l'API en attendant la garantie d'ajout
        reservationApi.addGame(reservationId, AddGameRequest(gameId, quantity))
        
        // Si tout s'est bien passé, on insère la donnée définitivement localement
        // Cela empêche l'app de demander une liste serveur qui n'est peut-être pas encore prête
        val validLink = ReservationGame(
            idReservation = reservationId,
            idGame = gameId,
            quantity = quantity
        )
        reservationGameDAO.insert(validLink)
    }

    suspend fun removeGameFromReservation(reservationId: Int, gameId: Int) {
        reservationApi.removeGame(reservationId, gameId)
        reservationGameDAO.deleteGameFromReservation(reservationId, gameId)
    }

    suspend fun addSuiviStep(reservationId: Int, status: String, commentaire: String?) {
        suiviApi.create(CreateSuiviRequest(status, commentaire, reservationId))
        refreshReservationDetail(reservationId)
    }

    suspend fun updateReservationGame(reservationId: Int, gameId: Int, quantity: Int, isPlaced: Boolean) {
        reservationApi.updateGameInReservation(reservationId, gameId, UpdateGameRequest(quantity, isPlaced))
        reservationGameDAO.updateGameQuantityAndPlacement(reservationId, gameId, quantity, isPlaced)
    }

}

private fun ReservationDetailDto.toEntity() = Reservation(
    idReservation = idReservation,
    idEditor = idEditor,
    festivalName = festivalName,
    status = status,
    nbSmallTables = nbSmallTables,
    nbLargeTables = nbLargeTables,
    nbCityHallTables = nbCityHallTables,
    typeAnimateur = typeAnimateur,
    listeDemandee = listeDemandee,
    listeRecue = listeRecue,
    jeuxRecus = jeuxRecus,
    m2 = m2,
    remise = remise,
    idTZ = idTZ
)



private fun ReservationGameDto.toLinkEntity() = ReservationGame(
    idReservation = idReservation,
    idGame = id,
    isGamePlaced = isGamePlaced,
    quantity = quantity
)

private fun SuiviReservationDto.toEntity() = SuiviReservation(
    id = id ?: 0,
    status = status,
    commentaire = commentaire,
    date = date,
    idReservation = idReservation
)





