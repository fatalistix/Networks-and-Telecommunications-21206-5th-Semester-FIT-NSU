package ru.nsu.vbalashov2.onlinesnake.controller.gamestate

data class GameEvent(
    val eventType: GameEventType,
    val attachment: Any
)
