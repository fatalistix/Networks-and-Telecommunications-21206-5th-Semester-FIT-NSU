package ru.nsu.vbalashov2.onlinesnake.ui.dto

import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig

data class AvailableGameDto(
    val gameName: String,
    val numOfPlayers: Int,
    val gameConfig: GameConfig,
    val canJoin: Boolean,
)