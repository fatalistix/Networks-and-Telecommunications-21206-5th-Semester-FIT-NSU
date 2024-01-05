package ru.nsu.vbalashov2.onlinesnake.net.dto.common

import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig

data class GameAnnouncement(
    val playerList: List<Player>,
    val gameConfig: GameConfig,
    val canJoin: Boolean,
    val gameName: String,
)