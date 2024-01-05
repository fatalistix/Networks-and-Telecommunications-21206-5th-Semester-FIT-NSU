package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import ru.nsu.vbalashov2.onlinesnake.controller.dto.MessageWithType
import ru.nsu.vbalashov2.onlinesnake.dto.Direction
import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.dto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.NodeRole
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.PlayerType
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.ui.GameUI
import ru.nsu.vbalashov2.onlinesnake.ui.dto.UpdateGameDto
import kotlin.math.max
import ru.nsu.vbalashov2.onlinesnake.ui.dto.Player as UIPlayer

class ClientGameState(
    private var masterSourceHost: SourceHost,
    private val gameName: String,
    private val playerName: String,
    private val gameUI: GameUI,
    private val gameConfig: GameConfig,
    defaultCoroutineScope: CoroutineScope,
    private val clientUpgrader: ClientUpgrader,
    private val gameExiter: GameExiter,
) : GameState {
    private val outComingConfirmMessageChannel = Channel<MessageWithType>(Channel.UNLIMITED)
    private val outComingNoConfirmMessageChannel = Channel<MessageWithType>(Channel.UNLIMITED)

    override val outComingConfirmMessageReceiveChannel: ReceiveChannel<MessageWithType> = outComingConfirmMessageChannel
    override val outComingNoConfirmMessageReceiveChannel: ReceiveChannel<MessageWithType> =
        outComingNoConfirmMessageChannel

    private var msgSeq = 0L
    private var masterID: Int = 0
    private var nodeID: Int = 0
    private var knownMsgSeq: Long = 0
    private var knownStateOrder: Int = 0

    private val eventChannel = Channel<GameEvent>(Channel.UNLIMITED)
    private var deputySourceHost: SourceHost? = null

    private var waitForJoin = true

    private val eventJob = defaultCoroutineScope.launch {
        joinToServer()
        while (true) {
            val gameEvent = eventChannel.receive()
            println(gameEvent.eventType)
            when (gameEvent.eventType) {
                GameEventType.EXIT -> {
                    gameExiter.exitGame()
                    return@launch
                }

                GameEventType.NETWORK_MESSAGE -> {
                    val rawMessage = gameEvent.attachment as RawMessage
                    handleNetworkMessage(rawMessage)
                }

                GameEventType.MAKE_PING -> {
                    val remoteSourceHost = gameEvent.attachment as SourceHost
                    handleMakePing(remoteSourceHost)
                }

                GameEventType.CONNECTION_LOST -> {
                    println("DEPUTY SOURCE HOST: $deputySourceHost")
                    if (deputySourceHost == null) {
                        exit()
                    } else {
                        masterSourceHost = deputySourceHost!!
                        knownMsgSeq = 0
                        outComingConfirmMessageChannel.send(MessageWithType(MessageType.PING, masterSourceHost, makePing(masterSourceHost)))
                    }
                }

                GameEventType.NEW_DIRECTION -> {
                    val direction = gameEvent.attachment as Direction
                    handleNewDirection(direction)
                }

                GameEventType.UPDATE_FIELD -> { }
            }
        }
    }

    private suspend fun handleNetworkMessage(rawMessage: RawMessage) {
        println(rawMessage.type)
        when (rawMessage.type) {
            MessageType.PING -> {
                if (rawMessage.sourceHost == masterSourceHost) {
                    val msgAck = makeAck(
                        masterSourceHost,
                        rawMessage.gameMessageInfo.msgSeq,
                        masterID
                    )
                    outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ACK, rawMessage.sourceHost, msgAck))
                    knownMsgSeq = max(knownMsgSeq, rawMessage.gameMessageInfo.msgSeq)
                } else {
                    val msgError = makeError(
                        rawMessage.sourceHost,
                        rawMessage.gameMessageInfo.msgSeq,
                        "unknown host"
                    )
                    outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ERROR, rawMessage.sourceHost, msgError))
                }
            }

            MessageType.STEER -> {
                val msgError = makeError(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    "I AM NOT A CENTER NODE"
                )
                outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ERROR, rawMessage.sourceHost, msgError))
            }

            MessageType.ACK -> {
                val msgAck = rawMessage.getAsAck()
                if (waitForJoin) {
                    this.nodeID = msgAck.gameMessageInfo.receiverID
                    waitForJoin = false
                }
            }

            MessageType.STATE -> {
                println("I AM HANDLING STATE")
                val msgState = rawMessage.getAsState()
                if (msgState.stateOrder <= knownStateOrder) {
                    println("STATE ORDERS: ${msgState.stateOrder} <= $knownStateOrder")
                    val msgAck = makeAck(
                        rawMessage.sourceHost,
                        rawMessage.gameMessageInfo.msgSeq,
                        receiverID = masterID
                    )
                    outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ACK, rawMessage.sourceHost, msgAck))
                    return
                }
                knownStateOrder = msgState.stateOrder
                gameUI.updateField(
                    UpdateGameDto(
                        stateOrder = msgState.stateOrder,
                        snakesList = msgState.snakeList,
                        foodList = msgState.foodList,
                        players = msgState.playerList.map { netPlayer ->
                            UIPlayer(
                                name = netPlayer.name,
                                score = netPlayer.score,
                                isMe = netPlayer.id == nodeID,
                                isMaster = !netPlayer.hasSourceHost
                            )
                        },
                        gameConfig = gameConfig,
                        myID = nodeID,
                        masterName = msgState.playerList.find {
                            !it.hasSourceHost
                        }?.name ?: gameName
                    )
                )
                val msgAck = makeAck(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    receiverID = masterID
                )
                outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ACK, rawMessage.sourceHost, msgAck))
                val newDeputyPlayer = msgState.playerList.find {
                    it.hasSourceHost && it.nodeRole == NodeRole.DEPUTY
                }
                if (newDeputyPlayer != null) {
                    if (newDeputyPlayer.id == nodeID) {
                        clientUpgrader.upgradeClient(
                            masterSourceHost,
                            gameName,
                            playerName,
                            gameConfig,
                            outComingConfirmMessageChannel,
                            outComingNoConfirmMessageChannel,
                            msgSeq,
                            masterID,
                            nodeID,
                            knownMsgSeq,
                            knownStateOrder,
                            eventChannel
                        )
                    } else {
                        deputySourceHost = newDeputyPlayer.sourceHost
                    }
                }
                println("I KNOW DEPUTY: $deputySourceHost")
            }

            MessageType.ANNOUNCEMENT -> {
                val msgError = makeError(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    "IN A WRONG IP:PORT"
                )
                outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ERROR, rawMessage.sourceHost, msgError))
            }

            MessageType.JOIN -> {
                val msgError = makeError(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    "i am not a center node"
                )
                outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ERROR, rawMessage.sourceHost, msgError))
            }

            MessageType.ERROR -> {
                if (rawMessage.gameMessageInfo.msgSeq == msgSeq) {
                    println("ERROR")
                    println(rawMessage.getAsError().errorMessage)
                }
                gameExiter.exitGame()
            }

            MessageType.ROLE_CHANGE -> {
                val msgRoleChange = rawMessage.getAsRoleChange()
                if (rawMessage.sourceHost == masterSourceHost) {
                    if (msgRoleChange.receiverRole == NodeRole.VIEWER) {
                        gameExiter.exitGame()
                    } else if (msgRoleChange.receiverRole == NodeRole.DEPUTY) {
                        outComingNoConfirmMessageChannel.send(MessageWithType(
                            MessageType.ACK,
                            masterSourceHost,
                            makeAck(masterSourceHost, msgRoleChange.gameMessageInfo.msgSeq, masterID)
                        ))
                        clientUpgrader.upgradeClient(
                            masterSourceHost,
                            gameName,
                            playerName,
                            gameConfig,
                            outComingConfirmMessageChannel,
                            outComingNoConfirmMessageChannel,
                            msgSeq,
                            masterID,
                            nodeID,
                            knownMsgSeq,
                            knownStateOrder,
                            eventChannel
                        )
                    }
                    return
                }
                if (rawMessage.sourceHost == deputySourceHost) {
                    masterSourceHost = rawMessage.sourceHost
                    knownMsgSeq = 0
                }
            }

            MessageType.DISCOVER -> {
                val msgError = makeError(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    "IN A WRONG IP:PORT"
                )
                outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ERROR, rawMessage.sourceHost, msgError))
            }

            MessageType.UNKNOWN -> {
                val msgError = makeError(
                    rawMessage.sourceHost,
                    rawMessage.gameMessageInfo.msgSeq,
                    "unknown message"
                )
                outComingNoConfirmMessageChannel.send(MessageWithType(MessageType.ERROR, rawMessage.sourceHost, msgError))
                gameExiter.exitGame()
            }
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
        outComingConfirmMessageChannel.send(MessageWithType(MessageType.JOIN, masterSourceHost, msgJoin))
    }

    private suspend fun handleMakePing(remoteSourceHost: SourceHost) {
        outComingConfirmMessageChannel.send(MessageWithType(MessageType.PING, remoteSourceHost, makePing(remoteSourceHost)))
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

    override suspend fun newDirection(direction: Direction) {
        eventChannel.send(GameEvent(GameEventType.NEW_DIRECTION, direction))
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
        outComingConfirmMessageChannel.send(MessageWithType(MessageType.STEER, masterSourceHost, msgSteer))
    }

    override suspend fun exit() {
        eventChannel.send(GameEvent(GameEventType.EXIT, GameEventType.EXIT))
    }

    override suspend fun ping(sourceHost: SourceHost) {
        eventChannel.send(GameEvent(GameEventType.MAKE_PING, sourceHost))
    }

    override suspend fun announce(sourceHost: SourceHost) { }

    override suspend fun connectionLost(sourceHost: SourceHost) {
        eventChannel.send(GameEvent(GameEventType.CONNECTION_LOST, sourceHost))
    }

    override suspend fun networkMessage(rawMessage: RawMessage) {
        eventChannel.send(GameEvent(GameEventType.NETWORK_MESSAGE, rawMessage))
    }

    override suspend fun cancel() {
        eventJob.cancel()
    }
}
