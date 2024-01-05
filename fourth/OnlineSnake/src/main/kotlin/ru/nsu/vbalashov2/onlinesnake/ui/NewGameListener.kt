package ru.nsu.vbalashov2.onlinesnake.ui

import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig

fun interface NewGameListener {
    fun newGame(gameConfig: GameConfig, gameName: String, playerName: String)
}