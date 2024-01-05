package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

import kotlinx.coroutines.channels.Channel
import ru.nsu.vbalashov2.onlinesnake.controller.dto.MessageWithType
import ru.nsu.vbalashov2.onlinesnake.dto.Coord
import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.dto.Snake
import ru.nsu.vbalashov2.onlinesnake.model.IDWithScore
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Player

fun interface MasterUpgrader {
    suspend fun upgradeToMaster(
        gameConfig: GameConfig,
        gameName: String,
        playerName: String,
        snakeList: List<Snake>,
        foodList: List<Coord>,
        scores: List<IDWithScore>,
        outComingConfirmMessageChannel: Channel<MessageWithType>,
        outComingNoConfirmMessageChannel: Channel<MessageWithType>,
        initMsgSeq: Long,
        stateOrder: Int,
        eventChannel: Channel<GameEvent>,
        netPlayersList: List<Player>,
        id: Int,
    )
}