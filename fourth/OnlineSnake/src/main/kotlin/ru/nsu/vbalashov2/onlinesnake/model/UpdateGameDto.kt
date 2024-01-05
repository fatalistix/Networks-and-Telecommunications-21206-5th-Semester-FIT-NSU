package ru.nsu.vbalashov2.onlinesnake.model

import ru.nsu.vbalashov2.onlinesnake.dto.Coord
import ru.nsu.vbalashov2.onlinesnake.dto.Direction
import ru.nsu.vbalashov2.onlinesnake.dto.Snake
import ru.nsu.vbalashov2.onlinesnake.dto.SnakeState

data class UpdateGameDto(
    val snakes: List<Snake>,
    val foodsPoint: List<Coord>,
    val idForRemoval: List<Int>
)