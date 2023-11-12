package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.model.SnakeGame
import ru.nsu.vbalashov2.onlinesnake.model.SnakeKey
import ru.nsu.vbalashov2.onlinesnake.proto.GameMessageKt
import ru.nsu.vbalashov2.onlinesnake.proto.gameAnnouncement
import ru.nsu.vbalashov2.onlinesnake.proto.gamePlayers
import ru.nsu.vbalashov2.onlinesnake.ui.ExitListener
import ru.nsu.vbalashov2.onlinesnake.ui.NewGameListener
import ru.nsu.vbalashov2.onlinesnake.ui.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint
import ru.nsu.vbalashov2.onlinesnake.ui.impl.GameUIPanel
import javax.swing.JFrame
import javax.swing.SwingUtilities


class Controller(private val mainAppFrame: JFrame) : NewGameListener, ExitListener {
    private val gameUI = GameUIPanel()
    private val players: MutableMap<Int, SnakeKey> = mutableMapOf()
    private var game: SnakeGame? = null
    private val swingKeyboardDirectionSourceCreator = SwingKeyboardDirectionSourceCreator()

    init {
        this.mainAppFrame.contentPane.add(gameUI)
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

    override fun newGame(gameConfig: GameConfig) {
        game = SnakeGame(
            width = gameConfig.width,
            height = gameConfig.height,
            foodStatic = gameConfig.foodStatic,
            stateDelayMs = gameConfig.stateDelayMs,
//            onFieldUpdate = { _, fieldArray ->
//                SwingUtilities.invokeLater {
//                    gameUI.updateField(fieldArray, gameConfig.width, gameConfig.height)
//                }
//            }
            onFieldUpdate = { snakes, food ->
                SwingUtilities.invokeLater {
                    gameUI.updateField(
                        snakes.map {
                            list ->
                            list.map { KeyPoint(it.x, it.y) }
                                   },
                        food.map { KeyPoint(it.x, it.y) },
                        gameConfig.width,
                        gameConfig.height,
                    )
                }
            }
        )
        game?.createSnake(swingKeyboardDirectionSourceCreator)
    }

    override fun exit() {
        if (game == null) {
            return
        }
        game?.close()
        game = null
    }
}