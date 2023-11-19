package ru.nsu.vbalashov2.onlinesnake.controller

import kotlinx.coroutines.*
import ru.nsu.vbalashov2.onlinesnake.model.SnakeGame
import ru.nsu.vbalashov2.onlinesnake.model.SnakeKey
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageEnd
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSource
import ru.nsu.vbalashov2.onlinesnake.net.dto.MessageType
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameAnnouncement
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Player
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.net.impl.ProtobufUDPSuspendMessageSourceEnd
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.GameMessage.AnnouncementMsg
import ru.nsu.vbalashov2.onlinesnake.ui.ExitListener
import ru.nsu.vbalashov2.onlinesnake.ui.GameUI
import ru.nsu.vbalashov2.onlinesnake.ui.NewGameListener
import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameInfo
import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint
import ru.nsu.vbalashov2.onlinesnake.ui.impl.GameFrame
import ru.nsu.vbalashov2.onlinesnake.ui.dto.GameConfig as UIGameConfig
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameConfig as NetGameConfig


class Controller : NewGameListener, ExitListener {
    private val gameUI: GameUI = GameFrame()
    private var game: SnakeGame? = null
    private val swingKeyboardDirectionSourceCreator = SwingKeyboardDirectionSourceCreator()

    private val multicastIP = "239.192.0.4"
    private val multicastPort = 9192
    private val multicastMessageSource: SuspendMessageSource = ProtobufUDPSuspendMessageSourceEnd(multicastIP, multicastPort)
    private val regularMessageSource: SuspendMessageSource
    private val regularMessageEnd: SuspendMessageEnd

    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val defaultCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val availableGames: MutableMap<String, GameAnnouncement> = mutableMapOf()

    private var announcementJob: Job? = null
    private val multicastListeningJob = ioCoroutineScope.launch {
        while (true) {
            val rawMessage = multicastMessageSource.readSuspend()
            when (rawMessage.getType()) {
                MessageType.ANNOUNCEMENT -> {
                    val msgAnnouncement = rawMessage.getAsAnnouncement()
                    msgAnnouncement.gameAnnouncementList.forEach { gameAnnouncement ->
                        gameUI.addAvailableGame(
                            AvailableGameInfo(
                                gameName = gameAnnouncement.gameName,
                                numOfPlayers = gameAnnouncement.playerList.size,
                                width = gameAnnouncement.gameConfig.width,
                                height = gameAnnouncement.gameConfig.height,
                                foodStatic = gameAnnouncement.gameConfig.foodStatic,
                                stateDelayMs = gameAnnouncement.gameConfig.stateDelayMs,
                                canJoin = gameAnnouncement.canJoin,
                            )
                        ) {
                            println("I've selected game")
                        }
                    }
                }
                MessageType.DISCOVER -> {

                }
                else -> {

                }
            }
        }
    }

    init {
        val sourceEnd = ProtobufUDPSuspendMessageSourceEnd()
        regularMessageSource = sourceEnd
        regularMessageEnd = sourceEnd
    }

    init {
        gameUI.addNewGameListener(this)
        gameUI.addExitListener(this)
    }

    init {
        gameUI.addWidthValidationRule {
            it in 10..100
        }
        gameUI.addHeightValidationRule {
            it in 10..100
        }
        gameUI.addFoodStaticValidationRule {
            it in 0..100
        }
        gameUI.addStateDelayMsValidationRule {
            it in 100..3000
        }
    }


    override fun newGame(uiGameConfig: UIGameConfig) {
        defaultCoroutineScope.launch {
            Server(
                messageSource = regularMessageSource,
                messageEnd = regularMessageEnd,
                gameConfig = NetGameConfig(
                    width = uiGameConfig.width,
                    height = uiGameConfig.height,
                    foodStatic = uiGameConfig.foodStatic,
                    stateDelayMs = uiGameConfig.stateDelayMs,
                ),
                gameUI = gameUI,
                coroutineScope = ioCoroutineScope,
                multicastIP = multicastIP,
                multicastPort = multicastPort,
                gameName = "SOME NAME"
            )
        }
//        exit()
//        game = SnakeGame(
//            width = uiGameConfig.width,
//            height = uiGameConfig.height,
//            foodStatic = uiGameConfig.foodStatic,
//            stateDelayMs = uiGameConfig.stateDelayMs,
//            onFieldUpdate = { snakes, food ->
//                gameUI.updateField(
//                    snakes.map { list ->
//                        list.map { KeyPoint(it.x, it.y) }
//                               },
//                    food.map { KeyPoint(it.x, it.y) },
//                    uiGameConfig.width,
//                    uiGameConfig.height,
//                )
//            }
//        )
//        game?.createSnake(swingKeyboardDirectionSourceCreator)
//        this.announcementJob = ioCoroutineScope.launch {
//            val msgAnnouncement = MsgAnnouncement(
//                sourceHost = SourceHost(multicastIP, multicastPort),
//                gameMessageInfo = GameMessageInfo(
//                    msgSeq = 0,
//                    senderID = 0,
//                    receiverID = 0,
//                    hasSenderID = false,
//                    hasReceiverId = false,
//                ),
//                listOf(GameAnnouncement(
//                    playerList = listOf(),
//                    gameConfig = NetGameConfig(
//                        width = uiGameConfig.width,
//                        height = uiGameConfig.height,
//                        foodStatic = uiGameConfig.foodStatic,
//                        stateDelayMs = uiGameConfig.stateDelayMs,
//                    ),
//                    canJoin = true,
//                    gameName = "something from xf",
//                )),
//            )
//            while (true) {
//                regularMessageEnd.writeAnnouncement(msgAnnouncement = msgAnnouncement)
//                delay(1000)
//            }
//        }
    }

    override fun exit() {
        if (game == null) {
            return
        }
        game?.close()
        game = null
        announcementJob?.cancel()
    }

    fun start() {
        gameUI.start()
    }
}