package com.example.festivalapp.data.editor.retrofit

import com.example.festivalapp.data.contact.retrofit.ContactDto
import com.example.festivalapp.data.game.retrofit.GameDto
import retrofit2.http.GET
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
}
