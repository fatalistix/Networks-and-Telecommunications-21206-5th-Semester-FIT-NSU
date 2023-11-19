package ru.nsu.vbalashov2.onlinesnake.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageEnd
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSource
import ru.nsu.vbalashov2.onlinesnake.net.dto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.*
import ru.nsu.vbalashov2.onlinesnake.ui.GameUI
import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint
import java.util.concurrent.TimeoutException
import javax.xml.transform.Source

class Client(
    private val messageSource: SuspendMessageSource,
    private val messageEnd: SuspendMessageEnd,
    private val masterIP: String,
    private val masterPort: Int,
    private val gameName: String,
    private val playerName: String,
    private val gameUI: GameUI,
    private val coroutineScope: CoroutineScope,
    private val gameConfig: GameConfig,
) {
    private val stateDelayMs = gameConfig.stateDelayMs.toLong()
    private val oneMessageDelayMs = this.stateDelayMs / 10
    private val timeoutDelayMs = (this.stateDelayMs * 0.8).toLong()

    private var msgSeq = 0L
    private var masterID: Int = 0
    private var nodeID: Int = 0

    private var sender: Job? = null
    private var sendChannel = Channel<ChannelElement>(Channel.UNLIMITED)

    @Throws(JoinFailException::class, TimeoutException::class)
    suspend fun join() {
        val msgJoin = MsgJoin(
            sourceHost = SourceHost(
                masterIP,
                masterPort,
            ),
            gameMessageInfo = GameMessageInfo(
                msgSeq = this.msgSeq++,
                senderID = 0,
                receiverID = 0,
                hasSenderID = false,
                hasReceiverId = true,
            ),
            playerType = PlayerType.HUMAN,
            gameName = gameName,
            playerName = playerName,
            requestedRole = NodeRole.NORMAL
        )

        val rawMessage = withTimeoutOrNull(timeoutDelayMs) {
            var rawMessage: RawMessage?
            do {
                messageEnd.writeJoin(msgJoin)
                rawMessage = withTimeoutOrNull(oneMessageDelayMs) {
                    messageSource.readSuspend()
                }
            } while (rawMessage == null)
            rawMessage
        } ?: throw TimeoutException("Timeout waiting for message")

        when (rawMessage.getType()) {
            MessageType.ACK -> {
                val ackMsg = rawMessage.getAsAck()
                masterID = ackMsg.gameMessageInfo.senderID
                nodeID = ackMsg.gameMessageInfo.receiverID
                listenGame()
            }
            MessageType.ERROR -> {
                val msgError = rawMessage.getAsError()
                throw JoinFailException("Got error message: ${msgError.errorMessage}")
            }
            else -> {
                throw JoinFailException("Unexpected message received")
            }
        }
    }

    suspend fun newDirection(direction: Direction) {
        sendChannel.send(ChannelElement(direction, MessageType.STEER))
    }

    suspend fun exitGame(direction: Direction) {
        sendChannel.send(ChannelElement(MessageType.ROLE_CHANGE, MessageType.ROLE_CHANGE))
    }

    private suspend fun listenGame() {
        this.sender = coroutineScope.launch {
            sendMessages()
        }

        while (true) {
            val rawMessage = messageSource.readSuspend()
            when (rawMessage.getType()) {
                MessageType.STATE -> {
                    val msgState = rawMessage.getAsState()
                    gameUI.updateField(
                        snakesKeyPointsList = msgState.snakeList.map { snake ->
                            snake.pointList.map { coord ->
                                KeyPoint(x = coord.x, y = coord.y)
                            }
                        },
                        foodList = msgState.foodList.map { coord -> KeyPoint(x = coord.x, y = coord.y ) },
                        width = gameConfig.width,
                        height = gameConfig.height,
                    )
                }
                else -> { }
            }
        }
    }

    private suspend fun sendMessages() {
        val msgPing = MsgPing(
            sourceHost = SourceHost(
                ip = masterIP,
                port = masterPort,
            ),
            gameMessageInfo = GameMessageInfo(
                msgSeq = this.msgSeq++,
                senderID = 0,
                receiverID = 0,
                hasSenderID = false,
                hasReceiverId = false,
            )
        )
        while (true) {
            val channelElement = withTimeoutOrNull(stateDelayMs) {
                sendChannel.receive()
            }
            if (channelElement == null) {

            } else {
                when (channelElement.type) {
                    MessageType.STEER -> {
                        val direction = channelElement.attachment as Direction
                        val msgSteer = MsgSteer(
                            sourceHost = SourceHost(
                                ip = masterIP,
                                port = masterPort,
                            ),
                            gameMessageInfo = GameMessageInfo(
                                msgSeq = this.msgSeq++,
                                senderID = 0,
                                receiverID = 0,
                                hasSenderID = false,
                                hasReceiverId = false,
                            ),
                            newDirection = direction,
                        )
                        val rawMessage = withTimeoutOrNull(timeoutDelayMs) {
                            var rawMessage: RawMessage?
                            do {
                                messageEnd.writeSteer(msgSteer)
                                rawMessage = withTimeoutOrNull(oneMessageDelayMs) {
                                    messageSource.readSuspend()
                                }
                            } while (rawMessage == null)
                            rawMessage
                        }
                        if (rawMessage == null) {

                        }
                    }
                    MessageType.ROLE_CHANGE -> {
                        val msgRoleChange = MsgRoleChange(
                            sourceHost = SourceHost(
                                ip = masterIP,
                                port = masterPort,
                            ),
                            gameMessageInfo = GameMessageInfo(
                                msgSeq = this.msgSeq++,
                                senderID = nodeID,
                                receiverID = masterID,
                                hasSenderID = true,
                                hasReceiverId = true,
                            ),
                            senderRole = NodeRole.VIEWER,
                            receiverRole = NodeRole.NORMAL,
                            hasSenderRole = true,
                            hasReceiverRole = false,
                        )
                        val rawMessage = withTimeoutOrNull(timeoutDelayMs) {
                            var rawMessage: RawMessage?
                            do {
                                messageEnd.writeRoleChange(msgRoleChange)
                                rawMessage = withTimeoutOrNull(oneMessageDelayMs) {
                                    messageSource.readSuspend()
                                }
                            } while (rawMessage == null)
                        }
                        if (rawMessage == null) {

                        }
                    }
                    else -> { }
                }
            }
        }
    }
}

private data class ChannelElement(
    val attachment: Any,
    val type: MessageType,
)