package ru.nsu.vbalashov2.onlinesnake.ui.dto

data class AvailableGameInfo(
    val gameName: String,
    val numOfPlayers: Int,
    val width: Int,
    val height: Int,
    val foodStatic: Int,
    val stateDelayMs: Int,
    val canJoin: Boolean,
)