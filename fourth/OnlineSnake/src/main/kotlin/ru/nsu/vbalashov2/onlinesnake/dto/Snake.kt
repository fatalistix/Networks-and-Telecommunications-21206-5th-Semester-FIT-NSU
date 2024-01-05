package ru.nsu.vbalashov2.onlinesnake.dto

data class Snake(
    val playerID: Int,
    val pointList: List<Coord>,
    val snakeState: SnakeState,
    val headDirection: Direction,
)