package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

import kotlinx.coroutines.channels.Channel
import ru.nsu.vbalashov2.onlinesnake.controller.dto.MessageWithType
import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

fun interface ViewerUpgrader {
    suspend fun upgradeToViewer(
        masterSourceHost: SourceHost,
        deputySourceHost: SourceHost?,
        gameConfig: GameConfig,
        outComingConfirmMessageChannel: Channel<MessageWithType>,
        outComingNoConfirmMessageChannel: Channel<MessageWithType>,
        initMsgSeq: Long,
        knownStateOrder: Int,
        masterID: Int,
        deputyID: Int,
        initViewerID: Int,
        viewerName: String,
        gameName: String,
        eventChannel: Channel<GameEvent>,
    )
}