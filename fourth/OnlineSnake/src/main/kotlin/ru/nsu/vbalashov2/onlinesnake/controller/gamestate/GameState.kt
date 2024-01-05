package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

import kotlinx.coroutines.channels.ReceiveChannel
import ru.nsu.vbalashov2.onlinesnake.controller.dto.MessageWithType
import ru.nsu.vbalashov2.onlinesnake.dto.Direction
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

interface GameState {
    suspend fun newDirection(direction: Direction)
    suspend fun exit()
    suspend fun ping(sourceHost: SourceHost)
    suspend fun announce(sourceHost: SourceHost)
    suspend fun connectionLost(sourceHost: SourceHost)
    suspend fun networkMessage(rawMessage: RawMessage)
    suspend fun cancel()
    val outComingConfirmMessageReceiveChannel: ReceiveChannel<MessageWithType>
    val outComingNoConfirmMessageReceiveChannel: ReceiveChannel<MessageWithType>
}