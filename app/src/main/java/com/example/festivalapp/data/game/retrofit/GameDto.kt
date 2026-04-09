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
    val minimumAge: Int,
    val duration: Int,
    val gameImage: String? = null,
    val idEditor: Int,
    // Champs supplémentaires du JSON qu'on ne stocke pas forcément en local
    val gameNotice: String? = null,
    val idGameType: Int? = null,
    val prototype: Boolean = false,
    val theme: String? = null,
    val description: String? = null,
    val rulesTutorial: String? = null,
    val edition: Int? = null
) {
    fun toRoom(): Game = Game(
        id = id,
        name = name,
        author = author,
        nbMinPlayer = nbMinPlayer,
        nbMaxPlayer = nbMaxPlayer,
        minimumAge = minimumAge,
        duration = duration,
        gameImage = gameImage,
        idEditor = idEditor
    )
}
