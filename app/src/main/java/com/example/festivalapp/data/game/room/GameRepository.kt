package com.example.festivalapp.data.game.room

import com.example.festivalapp.data.game.retrofit.GameApiService
import com.example.festivalapp.data.game.retrofit.GameDto
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val api: GameApiService,
    private val gameDAO: GameDAO
) {
    fun getAllGamesStream(): Flow<List<Game>> = gameDAO.getAllGames()

    suspend fun refreshGames() {
        try {
            val remoteGames = api.getAllGames()
            gameDAO.insertAll(remoteGames.map { it.toEntity() })
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getGamesByEditor(idEditor: Int): List<Game> {
        return try {
            val remote = api.getGamesByEditor(idEditor)
            gameDAO.insertAll(remote.map { it.toEntity() })
            gameDAO.getGamesByEditor(idEditor)
        } catch (e: Exception) {
            gameDAO.getGamesByEditor(idEditor)
        }
    }

    suspend fun searchGames(query: String): List<Game> {
        return gameDAO.searchGames(query)
    }

    suspend fun getGamesOfDistributeurs(): List<Game> {
        return api.getGamesOfDistributeurs().map { it.toEntity()}
    }


}

private fun GameDto.toEntity() = Game(
    id = id,
    name = name,
    author = author,
    nbMinPlayer = nbMinPlayer,
    nbMaxPlayer = nbMaxPlayer,
    gameNotice = gameNotice ?: "",
    idGameType = idGameType,
    minimumAge = minimumAge,
    prototype = prototype,
    duration = duration,
    theme = theme ?: "",
    description = description ?: "",
    gameImage = gameImage ?: "",
    rulesTutorial = rulesTutorial ?: "",
    edition = edition ?: 0,
    idEditor = idEditor
)
