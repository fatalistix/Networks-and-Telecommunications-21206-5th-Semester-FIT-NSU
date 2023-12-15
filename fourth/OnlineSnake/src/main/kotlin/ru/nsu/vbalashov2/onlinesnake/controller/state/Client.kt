package ru.nsu.vbalashov2.onlinesnake.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import ru.nsu.vbalashov2.onlinesnake.controller.proxy.ClientEventCreator
import ru.nsu.vbalashov2.onlinesnake.controller.proxy.ClientGameEvent
import ru.nsu.vbalashov2.onlinesnake.controller.proxy.ClientGameEventType
import ru.nsu.vbalashov2.onlinesnake.controller.proxy.MessagesProxy
import ru.nsu.vbalashov2.onlinesnake.dto.Direction
import ru.nsu.vbalashov2.onlinesnake.net.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.*
import ru.nsu.vbalashov2.onlinesnake.ui.GameUI
import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint
import kotlin.math.max

class Client(
    private val messageSender: SuspendMessageSender,
    private val messageReader: SuspendMessageReader,
    private val messageSerializer: SuspendMessageSerializer,
    private val messageDeserializer: SuspendMessageDeserializer,
    private var masterIP: String,
    private var masterPort: Int,
    private val gameName: String,
    private val playerName: String,
    private val gameUI: GameUI,
    private val defaultCoroutineScope: CoroutineScope,
    private val ioCoroutineScope: CoroutineScope,
    private val gameConfig: GameConfig,
) {
    private val stateDelayMs = gameConfig.stateDelayMs.toLong()

    private var msgSeq = 0L
    private var masterID: Int = 0
    private var nodeID: Int = 0
    private var knownMsgSeq: Long = 0
    private var masterSourceHost = SourceHost(masterIP, masterPort)

    private val eventChannel = Channel<ClientGameEvent>(Channel.UNLIMITED)

    private val clientEventCreator = ClientEventCreator(eventChannel)

    private val messagesProxy = MessagesProxy(
        messageDeserializer,
        messageSerializer,
        messageSender,
        messageReader,
        ioCoroutineScope,
        clientEventCreator,
        stateDelayMs
    )

    private var waitingForJoin = true

    private val eventJob = defaultCoroutineScope.launch {
        joinToServer()
        while (true) {
            val gameEvent = eventChannel.receive()
            println(gameEvent.eventType)
            when (gameEvent.eventType) {
                ClientGameEventType.EXIT -> {
                    messagesProxy.cancel()
                    return@launch
                }
                ClientGameEventType.NETWORK_MESSAGE -> {
                    val rawMessage = gameEvent.attachment as RawMessage
                    handleNetworkMessage(rawMessage)
                }
                ClientGameEventType.MAKE_PING -> {
                    val remoteSourceHost = gameEvent.attachment as SourceHost
                    handleMakePing(remoteSourceHost)
                }
                ClientGameEventType.CONNECTION_LOST -> TODO()
                ClientGameEventType.NEW_DIRECTION -> {
                    val direction = gameEvent.attachment as Direction
                    handleNewDirection(direction)
                }
            }
        }
    }

    private suspend fun handleNetworkMessage(rawMessage: RawMessage) {
        when (rawMessage.type) {
            MessageType.PING -> {
                if (rawMessage.sourceHost == masterSourceHost) {
                    val msgAck = makeAck(
                        masterSourceHost,
                        rawMessage.gameMessageInfo.msgSeq,
                        masterID
                    )
                    messagesProxy.sendMessage(MessageType.ACK, rawMessage.sourceHost, msgAck)
                    knownMsgSeq = max(knownMsgSeq, rawMessage.gameMessageInfo.msgSeq)
                } else {
                    val msgError = makeError(
                        rawMessage.sourceHost,
                        rawMessage.gameMessageInfo.msgSeq,
                        "unknown host"
                    )
                    messagesProxy.sendMessage(MessageType.ERROR, rawMessage.sourceHost, msgError)
                }
            }
            MessageType.STEER -> {
                val msgError = makeError(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    "I AM NOT A CENTER NODE"
                )
                messagesProxy.sendMessage(MessageType.ERROR, rawMessage.sourceHost, msgError)
            }
            MessageType.ACK -> {

            }
            MessageType.STATE -> {
                println("I AM HANDLING STATE")
                val msgState = rawMessage.getAsState()
                gameUI.updateField(
                    snakesKeyPointsList = msgState.snakeList.map { oneSnake ->
                        oneSnake.pointList.map {
                            KeyPoint(it.x, it.y)
                        }
                    },
                    foodList = msgState.foodList.map {
                        KeyPoint(it.x, it.y)
                    },
                    width = gameConfig.width,
                    height = gameConfig.height
                )
                val msgAck = makeAck(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    receiverID = masterID
                )
                messagesProxy.sendMessage(MessageType.ACK, rawMessage.sourceHost, msgAck)
            }
            MessageType.ANNOUNCEMENT -> TODO()
            MessageType.JOIN -> TODO()
            MessageType.ERROR -> {
                println("ERROR")
                println(rawMessage.getAsError().errorMessage)
            }
            MessageType.ROLE_CHANGE -> TODO()
            MessageType.DISCOVER -> TODO()
            MessageType.UNKNOWN -> TODO()
        }
    }

    private suspend fun joinToServer() {
        val msgJoin = MsgJoin(
            sourceHost = masterSourceHost,
            gameMessageInfo = GameMessageInfo(
                msgSeq = msgSeq++,
                senderID = 0,
                receiverID = 0,
                hasSenderID = false,
                hasReceiverId = false
            ),
            playerType = PlayerType.HUMAN,
            playerName = playerName,
            gameName = gameName,
            requestedRole = NodeRole.NORMAL,
        )
        messagesProxy.sendMessage(MessageType.JOIN, masterSourceHost, msgJoin)
    }

    private suspend fun handleMakePing(remoteSourceHost: SourceHost) {
        messagesProxy.sendMessage(MessageType.PING, remoteSourceHost, makePing(remoteSourceHost))
    }

    private fun makePing(sourceHost: SourceHost): MsgPing {
        return MsgPing(
            sourceHost = sourceHost,
            gameMessageInfo = GameMessageInfo(
                msgSeq = msgSeq++,
                senderID = 0,
                receiverID = 0,
                hasReceiverId = false,
                hasSenderID = false,
            )
        )
    }

    private fun makeAck(sourceHost: SourceHost, requestMsgSeq: Long, receiverID: Int): MsgAck {
        return MsgAck(
            sourceHost = sourceHost,
            gameMessageInfo = GameMessageInfo(
                msgSeq = requestMsgSeq,
                senderID = nodeID,
                receiverID = receiverID,
                hasSenderID = true,
                hasReceiverId = true,
            )
        )
    }

    private fun makeError(sourceHost: SourceHost, requestMsgSeq: Long, message: String): MsgError {
        return MsgError(
            sourceHost = sourceHost,
            gameMessageInfo = GameMessageInfo(
                msgSeq = requestMsgSeq,
                senderID = 0,
                receiverID = 0,
                hasSenderID = false,
                hasReceiverId = false,
            ),
            errorMessage = message,
        )
    }

    suspend fun newDirection(direction: Direction) {
        eventChannel.send(ClientGameEvent(ClientGameEventType.NEW_DIRECTION, direction))
    }

    private suspend fun handleNewDirection(direction: Direction) {
        val msgSteer = MsgSteer(
            masterSourceHost,
            GameMessageInfo(
                msgSeq++,
                0,
                0,
                hasSenderID = false,
                hasReceiverId = false
            ),
            direction
        )
        messagesProxy.sendMessage(MessageType.STEER, masterSourceHost, msgSteer)
    }

//    @Throws(JoinFailException::class, TimeoutException::class)
//    suspend fun join() {
//        val msgJoin = MsgJoin(
//            sourceHost = SourceHost(
//                masterIP,
//                masterPort,
//            ),
//            gameMessageInfo = GameMessageInfo(
//                msgSeq = this.msgSeq++,
//                senderID = 0,
//                receiverID = 0,
//                hasSenderID = false,
//                hasReceiverId = true,
//            ),
//            playerType = PlayerType.HUMAN,
//            gameName = gameName,
//            playerName = playerName,
//            requestedRole = NodeRole.NORMAL
//        )
//
//        val rawMessage = withTimeoutOrNull(timeoutDelayMs) {
//            var rawMessage: RawMessage?
//            do {
//                messageEnd.writeJoin(msgJoin)
//                rawMessage = withTimeoutOrNull(oneMessageDelayMs) {
//                    messageSource.readSuspend()
//                }
//            } while (rawMessage == null)
//            rawMessage
//        } ?: throw TimeoutException("Timeout waiting for message")
//
//        when (rawMessage.type) {
//            MessageType.ACK -> {
//                val ackMsg = rawMessage.getAsAck()
//                masterID = ackMsg.gameMessageInfo.senderID
//                nodeID = ackMsg.gameMessageInfo.receiverID
//                listenGame()
//            }
//            MessageType.ERROR -> {
//                val msgError = rawMessage.getAsError()
//                throw JoinFailException("Got error message: ${msgError.errorMessage}")
//            }
//            else -> {
//                throw JoinFailException("Unexpected message received")
//            }
//        }
//    }
//
//    suspend fun newDirection(direction: Direction) {
//        sendChannel.send(ChannelElement(direction, MessageType.STEER))
//    }
//
//    suspend fun exitGame() {
//        sendChannel.send(ChannelElement(MessageType.ROLE_CHANGE, MessageType.ROLE_CHANGE))
//    }
//
//    private suspend fun listenGame() {
//        this.sender = coroutineScope.launch {
//            sendMessages()
//        }
//
//        while (true) {
//            val rawMessage = messageSource.readSuspend()
//            when (rawMessage.type) {
//                MessageType.STATE -> {
//                    val msgState = rawMessage.getAsState()
//                    gameUI.updateField(
//                        snakesKeyPointsList = msgState.snakeList.map { snake ->
//                            snake.pointList.map { coord ->
//                                KeyPoint(x = coord.x, y = coord.y)
//                            }
//                        },
//                        foodList = msgState.foodList.map { coord -> KeyPoint(x = coord.x, y = coord.y ) },
//                        width = gameConfig.width,
//                        height = gameConfig.height,
//                    )
//                }
//                else -> { }
//            }
//        }
//    }
//
//    private suspend fun sendMessages() {
//        val msgPing = MsgPing(
//            sourceHost = SourceHost(
//                ip = masterIP,
//                port = masterPort,
//            ),
//            gameMessageInfo = GameMessageInfo(
//                msgSeq = this.msgSeq++,
//                senderID = 0,
//                receiverID = 0,
//                hasSenderID = false,
//                hasReceiverId = false,
//            )
//        )
//        while (true) {
//            val channelElement = withTimeoutOrNull(stateDelayMs) {
//                sendChannel.receive()
//            }
//            if (channelElement == null) {
//
//            } else {
//                when (channelElement.type) {
//                    MessageType.STEER -> {
//                        val direction = channelElement.attachment as Direction
//                        val msgSteer = MsgSteer(
//                            sourceHost = SourceHost(
//                                ip = masterIP,
//                                port = masterPort,
//                            ),
//                            gameMessageInfo = GameMessageInfo(
//                                msgSeq = this.msgSeq++,
//                                senderID = 0,
//                                receiverID = 0,
//                                hasSenderID = false,
//                                hasReceiverId = false,
//                            ),
//                            newDirection = direction,
//                        )
//                        val rawMessage = withTimeoutOrNull(timeoutDelayMs) {
//                            var rawMessage: RawMessage?
//                            do {
//                                messageEnd.writeSteer(msgSteer)
//                                rawMessage = withTimeoutOrNull(oneMessageDelayMs) {
//                                    messageSource.readSuspend()
//                                }
//                            } while (rawMessage == null)
//                            rawMessage
//                        }
//                        if (rawMessage == null) {
//
//                        }
//                    }
//                    MessageType.ROLE_CHANGE -> {
//                        val msgRoleChange = MsgRoleChange(
//                            sourceHost = SourceHost(
//                                ip = masterIP,
//                                port = masterPort,
//                            ),
//                            gameMessageInfo = GameMessageInfo(
//                                msgSeq = this.msgSeq++,
//                                senderID = nodeID,
//                                receiverID = masterID,
//                                hasSenderID = true,
//                                hasReceiverId = true,
//                            ),
//                            senderRole = NodeRole.VIEWER,
//                            receiverRole = NodeRole.NORMAL,
//                            hasSenderRole = true,
//                            hasReceiverRole = false,
//                        )
//                        val rawMessage = withTimeoutOrNull(timeoutDelayMs) {
//                            var rawMessage: RawMessage?
//                            do {
//                                messageEnd.writeRoleChange(msgRoleChange)
//                                rawMessage = withTimeoutOrNull(oneMessageDelayMs) {
//                                    messageSource.readSuspend()
//                                }
//                            } while (rawMessage == null)
//                        }
//                        if (rawMessage == null) {
//
//                        }
//                    }
//                    else -> { }
//                }
//            }
//        }
//    }
}
//
//private data class ChannelElement(
//    val attachment: Any,
//    val type: MessageType,
//)