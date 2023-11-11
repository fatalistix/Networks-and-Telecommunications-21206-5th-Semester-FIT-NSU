package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.model.SnakeGame
import ru.nsu.vbalashov2.onlinesnake.model.SnakeKey
import ru.nsu.vbalashov2.onlinesnake.ui.ExitListener
import ru.nsu.vbalashov2.onlinesnake.ui.NewGameListener
import ru.nsu.vbalashov2.onlinesnake.ui.dto.GameConfig
import ru.nsu.vbalashov2.onlinesnake.ui.impl.GameUIPanel
import javax.swing.JFrame
import javax.swing.SwingUtilities


class Controller(private val mainAppFrame: JFrame) : NewGameListener, ExitListener {
    private val gameUI = GameUIPanel()
    private val players: MutableMap<Int, SnakeKey> = mutableMapOf()
    private var game: SnakeGame? = null

    init {
        this.mainAppFrame.contentPane.add(gameUI)
    }

    init {
        gameUI.addNewGameListener(this)
        gameUI.addExitListener(this)
    }

    override fun newGame(gameConfig: GameConfig) {
        game = SnakeGame(
            width = gameConfig.width,
            height = gameConfig.height,
            foodStatic = gameConfig.foodStatic,
            stateDelayMs = gameConfig.stateDelayMs,
            onFieldUpdate = { _, fieldArray ->
                SwingUtilities.invokeLater {
                    gameUI.updateField(fieldArray, gameConfig.width, gameConfig.height)
                }
            }
        )
    }

    override fun exit() {
        if (game == null) {
            return
        }
        game?.close()
        game = null
    }
}