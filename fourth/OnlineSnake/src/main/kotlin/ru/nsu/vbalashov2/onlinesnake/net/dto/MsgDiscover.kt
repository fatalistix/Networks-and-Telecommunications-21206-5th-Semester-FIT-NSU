package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo

data class MsgDiscover(
    val sourceHost: SourceHost,
    val gameMessageInfo: GameMessageInfo,
)
