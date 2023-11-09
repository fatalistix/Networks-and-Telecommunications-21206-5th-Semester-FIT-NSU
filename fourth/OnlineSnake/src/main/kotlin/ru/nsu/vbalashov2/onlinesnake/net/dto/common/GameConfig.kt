package ru.nsu.vbalashov2.onlinesnake.net.dto.common

data class GameConfig(
    val width: Int,
    val height: Int,
    val foodStatic: Int,
    val stateDelayMs: Int,
)