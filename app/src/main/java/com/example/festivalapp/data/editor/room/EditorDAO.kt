package com.example.festivalapp.data.editor.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EditorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(editors: List<Editor>)

    @Query("SELECT * FROM editors ORDER BY name")
    fun getAllEditors(): Flow<List<Editor>>

    @Query("SELECT * FROM editors WHERE id = :editorId")
    fun getEditorById(editorId: Int): Flow<Editor?>
}

