package com.hamster.happyness.viewmodel


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameQuickEnterModel(
    @SerialName("cid")
    val cid: String = "",
    @SerialName("gameList")
    val gameList: List<Game> = listOf(),
    @SerialName("name")
    val name: String = ""
) {
    @Serializable
    data class Game(
        @SerialName("gameId")
        val gameId: String = "",
        @SerialName("logoUrl")
        val logoUrl: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("playUrl")
        val playUrl: String = "",
        @SerialName("supportScreen")
        val supportScreen: String = "",
        @SerialName("type")
        val type: String = ""
    )
}