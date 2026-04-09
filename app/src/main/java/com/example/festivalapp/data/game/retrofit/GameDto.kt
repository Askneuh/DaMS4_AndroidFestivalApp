package com.example.festivalapp.data.game.retrofit

import com.example.festivalapp.data.game.room.Game
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int,
    val name: String,
    val author: String,
    val nbMinPlayer: Int,
    val nbMaxPlayer: Int,
    val gameNotice: String? = null,
    val idGameType: Int? = null,
    val minimumAge: Int,
    val prototype: Boolean = false,
    val duration: Int,
    val theme: String? = null,
    val description: String? = null,
    val gameImage: String? = null,
    val rulesTutorial: String? = null,
    val edition: Int? = null,
    val idEditor: Int
) {
    fun toRoom(): Game = Game(
        id = id,
        name = name,
        author = author,
        nbMinPlayer = nbMinPlayer,
        nbMaxPlayer = nbMaxPlayer,
        gameNotice = gameNotice ?: "",
        idGameType = idGameType ?: 0,
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
}
