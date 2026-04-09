package com.example.festivalapp.data.editor.retrofit

import com.example.festivalapp.data.editor.room.Editor
import kotlinx.serialization.Serializable

@Serializable
data class EditorDto(
    val id: Int,
    val name: String,
    val exposant: Boolean,
    val distributeur: Boolean,
    val logo: String?
) {
    fun toRoom(): Editor = Editor(
        id = id,
        name = name,
        exposant = exposant,
        distributeur = distributeur,
        logo = logo
    )
}
