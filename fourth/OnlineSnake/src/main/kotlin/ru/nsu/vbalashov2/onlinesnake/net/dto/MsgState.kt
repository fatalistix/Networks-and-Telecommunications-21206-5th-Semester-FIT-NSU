package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.dto.Coord
import ru.nsu.vbalashov2.onlinesnake.dto.Snake
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

