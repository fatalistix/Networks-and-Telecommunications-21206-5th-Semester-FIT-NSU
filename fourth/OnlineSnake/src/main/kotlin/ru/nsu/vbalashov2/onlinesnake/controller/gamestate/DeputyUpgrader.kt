package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

import kotlinx.coroutines.channels.Channel
import ru.nsu.vbalashov2.onlinesnake.controller.dto.MessageWithType
import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

fun interface DeputyUpgrader {
    suspend fun upgradeToDeputy(
        masterSourceHost: SourceHost,
        gameName: String,
        playerName: String,
        gameConfig: GameConfig,
        outComingConfirmMessageChannel: Channel<MessageWithType>,
        outComingNoConfirmMessageChannel: Channel<MessageWithType>,
        msgSeq: Long,
        masterID: Int,
        nodeID: Int,
        knownStateOrder: Int,
        eventChannel: Channel<GameEvent>,
    )
}