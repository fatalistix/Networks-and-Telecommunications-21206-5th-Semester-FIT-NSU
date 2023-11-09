package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.model.SnakeGame
import ru.nsu.vbalashov2.onlinesnake.model.SnakeKey
import ru.nsu.vbalashov2.onlinesnake.ui.GameUI
import ru.nsu.vbalashov2.onlinesnake.ui.impl.GameUIPanel
import javax.swing.JFrame
import javax.swing.SwingUtilities


class Controller(private val mainAppFrame: JFrame) {
    private val gameUI = GameUIPanel()
    private val players: MutableMap<Int, SnakeKey> = mutableMapOf()

    init {
        this.mainAppFrame.contentPane.add(gameUI)
    }

    private val game = SnakeGame(10, 10, 1_000) { snakes, fieldArray ->
        SwingUtilities.invokeLater {
            gameUI.updateField(fieldArray, 10, 10)
        }
    }

    init {
        val key = game.createSnake(SwingKeyboardDirectionSourceCreator())
        players[0] = key
    }
}