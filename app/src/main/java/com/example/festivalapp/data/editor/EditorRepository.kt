package com.example.festivalapp.data.editor

import com.example.festivalapp.data.contact.room.Contact
import com.example.festivalapp.data.contact.room.ContactDAO
import com.example.festivalapp.data.editor.retrofit.EditorApiService
import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.editor.room.EditorDAO
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameDAO
import kotlinx.coroutines.flow.Flow

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
        val dtoList = api.getGamesByEditor(editorId)
        gameDAO.insertAll(dtoList.map { it.toRoom() })
    }

    suspend fun refreshContactsForEditor(editorId: Int) {
        val dtoList = api.getContactsByEditor(editorId)
        contactDAO.insertAll(dtoList.map { it.toRoom() })
    }
}
