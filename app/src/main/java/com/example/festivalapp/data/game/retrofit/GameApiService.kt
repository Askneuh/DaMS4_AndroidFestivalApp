package com.example.festivalapp.data.game.retrofit

import retrofit2.http.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateGameResponse(val message: String, val id: Int? = null)

interface GameApiService {

    @GET("games")
    suspend fun getAllGames(): List<GameDto>

    @GET("games/{gameId}")
    suspend fun getGameById(@Path("gameId") gameId: Int): GameDto

    @GET("games/byEditor/{idEditor}")
    suspend fun getGamesByEditor(@Path("idEditor") idEditor: Int): List<GameDto>

    @GET("games/notByEditor/{idEditor}")
    suspend fun getGamesNotByEditor(@Path("idEditor") idEditor: Int): List<GameDto>

    @POST("games")
    suspend fun createGame(@Body game: GameDto): CreateGameResponse

    @POST("games/update/{gameId}")
    suspend fun updateGame(
        @Path("gameId") gameId: Int,
        @Body fields: Map<String, @JvmSuppressWildcards Any?>
    ): CreateGameResponse

    @DELETE("games/{gameId}")
    suspend fun deleteGame(@Path("gameId") gameId: Int)

    @GET("games/byDistributeurs")
    suspend fun getGamesOfDistributeurs(): List<GameDto>
}
