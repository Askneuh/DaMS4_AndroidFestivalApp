package com.example.festivalapp.data.editor.retrofit

import com.example.festivalapp.data.contact.retrofit.ContactDto
import com.example.festivalapp.data.game.retrofit.GameDto
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EditorApiService {

    // GET /editeurs/ → liste tous les éditeurs
    @GET("editeurs/")
    suspend fun getAllEditors(): List<EditorDto>

    // GET /editeurs/{editorId} → un éditeur par son ID
    @GET("editeurs/{editorId}")
    suspend fun getEditorById(@Path("editorId") editorId: Int): EditorDto

    // GET /games/byEditor/{idEditor} → les jeux d'un éditeur
    @GET("games/byEditor/{idEditor}")
    suspend fun getGamesByEditor(@Path("idEditor") idEditor: Int): List<GameDto>

    // GET /contacts/editor/{editorId} → les contacts d'un éditeur
    @GET("contacts/editor/{editorId}")
    suspend fun getContactsByEditor(@Path("editorId") editorId: Int): List<ContactDto>

    // POST /games → créer un jeu
    @POST("games")
    suspend fun createGame(@Body request: CreateGameRequest): CreateGameResponse

    // POST /games/update/{gameId} → mettre à jour un jeu
    @POST("games/update/{gameId}")
    suspend fun updateGame(@Path("gameId") gameId: Int, @Body request: CreateGameRequest): GeneralResponse

    // DELETE /games/{gameId} → supprimer un jeu
    @DELETE("games/{gameId}")
    suspend fun deleteGame(@Path("gameId") gameId: Int): GeneralResponse
}

@Serializable
data class CreateGameRequest(
    val name: String,
    val author: String,
    val nbMinPlayer: Int,
    val nbMaxPlayer: Int,
    val minimumAge: Int,
    val duration: Int,
    val idEditor: Int,
    val gameImage: String? = null,
    val prototype: Boolean = false,
    val theme: String? = null,
    val description: String? = null,
    val gameNotice: String? = null,
    val rulesTutorial: String? = null,
    val edition: Int? = null,
    val idGameType: Int? = null
)

@Serializable
data class CreateGameResponse(
    val message: String,
    val id: Int
)

@Serializable
data class GeneralResponse(
    val message: String
)
