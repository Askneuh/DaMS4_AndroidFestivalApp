package com.example.festivalapp.data.editor.room

import com.example.festivalapp.data.contact.room.Contact
import com.example.festivalapp.data.contact.room.ContactDAO
import com.example.festivalapp.data.editor.retrofit.EditorApiService
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameDAO
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

import com.example.festivalapp.data.editor.retrofit.CreateContactRequest
import com.example.festivalapp.data.editor.retrofit.CreateGameRequest

class EditorRepository(
    private val editorDAO: EditorDAO,
    private val gameDAO: GameDAO,
    private val contactDAO: ContactDAO,
    private val api: EditorApiService
) {
    fun getAllEditors(): Flow<List<Editor>> {
        return editorDAO.getAllEditors()
    }

    fun getEditorById(editorId: Int): Flow<Editor?> {
        return editorDAO.getEditorById(editorId)
    }

    fun getGamesByEditor(editorId: Int): Flow<List<Game>> {
        return gameDAO.getGamesByEditor(editorId)
    }

    fun getContactsByEditor(editorId: Int): Flow<List<Contact>> {
        return contactDAO.getContactsByEditor(editorId)
    }

    suspend fun refreshEditors() {
        val dtoList = api.getAllEditors()
        editorDAO.insertAll(dtoList.map { it.toRoom() })
    }

    suspend fun refreshGamesForEditor(editorId: Int) {
        try {
            val dtoList = api.getGamesByEditor(editorId)
            gameDAO.insertAll(dtoList.map { it.toRoom() })
        } catch (e: Exception) {
            // Si c'est une 404, on ignore (pas de jeux), sinon on remonte l'erreur
            if (e is HttpException && e.code() == 404) return
            throw e
        }
    }

    suspend fun refreshContactsForEditor(editorId: Int) {
        try {
            val dtoList = api.getContactsByEditor(editorId)
            contactDAO.insertAll(dtoList.map { it.toRoom() })
        } catch (e: Exception) {
            // Idem pour les contacts
            if (e is HttpException && e.code() == 404) return
            throw e
        }
    }

    suspend fun addGame(request: CreateGameRequest) {
        api.createGame(request)
        // Rafraîchir la liste des jeux locaux depuis l'API
        refreshGamesForEditor(request.idEditor)
    }

    suspend fun updateGame(gameId: Int, request: CreateGameRequest) {
        api.updateGame(gameId, request)
        refreshGamesForEditor(request.idEditor)
    }

    suspend fun deleteGame(gameId: Int, editorId: Int) {
        api.deleteGame(gameId)
        // Optionnel : on peut supprimer localement pour être plus rapide, ou juste rafraîchir
        gameDAO.deleteGameById(gameId)
        refreshGamesForEditor(editorId)
    }

    suspend fun addContact(request: CreateContactRequest) {
        api.createContact(request)
        refreshContactsForEditor(request.idEditor)
    }

    suspend fun updateContact(contactId: Int, request: CreateContactRequest) {
        api.updateContact(contactId, request)
        refreshContactsForEditor(request.idEditor)
    }

    suspend fun deleteContact(contactId: Int, editorId: Int) {
        api.deleteContact(contactId)
        contactDAO.deleteContactById(contactId)
        refreshContactsForEditor(editorId)
    }
}