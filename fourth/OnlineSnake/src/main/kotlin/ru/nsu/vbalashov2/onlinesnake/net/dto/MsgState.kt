package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Direction
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Player
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

data class MsgState(
    val sourceHost: SourceHost,
    val gameMessageInfo: GameMessageInfo,
    val stateOrder: Int,
    val snakeList: List<Snake>,
    val foodList: List<Coord>,
    val playerList: List<Player>,
)

data class Coord(
    val x: Int,
    val y: Int,
)

data class Snake(
    val playerID: Int,
    val pointList: List<Coord>,
    val snakeState: SnakeState,
    val headDirection: Direction,
)

enum class SnakeState {
    ALIVE,
    ZOMBIE,
}
