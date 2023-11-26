package ru.nsu.vbalashov2.onlinesnake.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.nsu.vbalashov2.onlinesnake.model.SnakeGame
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageEnd
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSource
import ru.nsu.vbalashov2.onlinesnake.net.dto.MessageType
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgError
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.*
import ru.nsu.vbalashov2.onlinesnake.ui.GameUI

class Server(
    private val messageSource: SuspendMessageSource,
    private val messageEnd: SuspendMessageEnd,
    private val gameConfig: GameConfig,
    private val gameUI: GameUI,
    private val ioCoroutineScope: CoroutineScope,
    private val defaultCoroutineScope: CoroutineScope,
    private val multicastIP: String,
    private val multicastPort: Int,
    private val gameName: String,
) {
    @Volatile
    private var msgSeq: Long = 0
    private val multicastSourceHost = SourceHost(multicastIP, multicastPort)
    private val announcementDelayMs = 1000L

    private val playersMap = mutableMapOf<String, String>()
    private val viewersMap = mutableMapOf<String, String>()

    private val snakeGame = SnakeGame(
        width = gameConfig.width,
        height = gameConfig.height,
        foodStatic = gameConfig.foodStatic,
        stateDelayMs = gameConfig.stateDelayMs,
    )

    private val announcementJob = ioCoroutineScope.launch {
        while (true) {
            sendAnnouncement(multicastSourceHost)
            delay(announcementDelayMs)
        }
    }

    private val eventJob = defaultCoroutineScope.launch {
        while (true) {

        }
    }




    suspend fun listenMessages() {
        while (true) {
            val rawMessage = messageSource.readSuspend()
            when (rawMessage.type) {
                MessageType.JOIN -> {
                    handleJoin(rawMessage)
                }
                MessageType.STEER -> {
                    handleSteer(rawMessage)
                }
                MessageType.ROLE_CHANGE -> {
                    handleRoleChange(rawMessage)
                }
                MessageType.PING -> {
                    handlePing(rawMessage)
                }
                else -> {
                    handleUnexpected(rawMessage)
                }
            }
        }
    }

    suspend fun handleJoin(rawMessage: RawMessage) {
        val msgJoin = rawMessage.getAsJoin()
        when (msgJoin.requestedRole) {
            NodeRole.NORMAL -> {
                if (msgJoin.playerName in playersMap) {
                    sendError("player with name ${msgJoin.playerName} already exists", msgJoin.sourceHost)
                }

            }
            else -> { }
        }
    }

    private suspend fun handleSteer(rawMessage: RawMessage) {

    }

    private suspend fun handleRoleChange(rawMessage: RawMessage) {

    }

    private suspend fun handlePing(rawMessage: RawMessage) {

    }

    private suspend fun handleUnexpected(rawMessage: RawMessage) {
        sendError("unexpected message", rawMessage.sourceHost)
    }

    suspend fun sendAnnouncement(sourceHost: SourceHost) {
        val msgAnnouncement = MsgAnnouncement(
            sourceHost = sourceHost,
            gameMessageInfo = GameMessageInfo(
                msgSeq = msgSeq++,
                senderID = 0,
                receiverID = 0,
                hasSenderID = false,
                hasReceiverId = false,
            ),
            gameAnnouncementList = listOf(GameAnnouncement(
                playerList = listOf(),
                gameConfig = gameConfig,
                canJoin = true,
                gameName = gameName
            ))
        )
        messageEnd.writeAnnouncement(msgAnnouncement)
    }

    suspend fun sendError(message: String, sourceHost: SourceHost) {
        val msgError = MsgError(
            sourceHost = sourceHost,
            gameMessageInfo = GameMessageInfo(
                msgSeq = msgSeq++,
                senderID = 0,
                receiverID = 0,
                hasReceiverId = false,
                hasSenderID = false,
            ),
            errorMessage = message,
        )
        messageEnd.writeError(msgError)
    }
}


//{ snakesKeyPointsList, foodList ->
//    gameUI.updateField(
//        snakesKeyPointsList = snakesKeyPointsList.map { list ->
//            list.map { KeyPoint(it.x, it.y) }
//        },
//        foodList = foodList.map { KeyPoint(it.x, it.y) },
//        width = gameConfig.width,
//        height = gameConfig.height,
//    )
//}