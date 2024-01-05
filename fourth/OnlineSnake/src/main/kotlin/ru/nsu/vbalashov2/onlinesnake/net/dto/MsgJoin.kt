package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.*

data class MsgJoin(
    val sourceHost: SourceHost,
    val gameMessageInfo: GameMessageInfo,
    val playerType: PlayerType,
    val playerName: String,
    val gameName: String,
    val requestedRole: NodeRole,
)
