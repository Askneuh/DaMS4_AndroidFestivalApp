package com.example.festivalapp.data.game.retrofit

<<<<<<< HEAD
import com.example.festivalapp.data.game.room.Game
=======
>>>>>>> 82c0fb8 (a lot of things)
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int,
    val name: String,
    val author: String,
    val nbMinPlayer: Int,
    val nbMaxPlayer: Int,
<<<<<<< HEAD
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
=======
    val gameNotice: String? = null,
    val idGameType: Int,
    val minimumAge: Int,
    val prototype: Boolean,
    val duration: Int,
    val theme: String? = null,
    val description: String? = null,
    val gameImage: String? = null,
    val rulesTutorial: String? = null,
    val edition: Int? = null,
    val idEditor: Int
)
>>>>>>> 82c0fb8 (a lot of things)
