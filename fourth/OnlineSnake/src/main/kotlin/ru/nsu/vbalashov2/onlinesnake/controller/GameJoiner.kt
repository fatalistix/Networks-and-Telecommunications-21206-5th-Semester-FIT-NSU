package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameDto

fun interface GameJoiner {
    fun joinGame(availableGameDto: AvailableGameDto, playerName: String, masterSourceHost: SourceHost)
}