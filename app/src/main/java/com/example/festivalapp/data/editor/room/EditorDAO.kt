package com.example.festivalapp.data.editor.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EditorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(editors: List<Editor>)

}
