package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

fun interface GameExiter {
    suspend fun exitGame()
}