package ru.nsu.vbalashov2.onlinesnake.ui.dto

import ru.nsu.vbalashov2.onlinesnake.dto.Coord
import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.dto.Snake

data class UpdateGameDto(
    val stateOrder: Int,
    val snakesList: List<Snake>,
    val foodList: List<Coord>,
    val players: List<Player>,
    val gameConfig: GameConfig,
    val myID: Int,
    val masterName: String,
)
