package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

data class MsgError(
    val sourceHost: SourceHost,
    val gameMessageInfo: GameMessageInfo,
    val errorMessage: String,
)