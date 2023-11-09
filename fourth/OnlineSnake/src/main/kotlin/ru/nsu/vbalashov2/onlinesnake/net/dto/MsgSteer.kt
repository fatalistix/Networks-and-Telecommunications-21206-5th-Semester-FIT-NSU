package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.model.Direction

data class Steer (
    val sourceHost: SourceHost,
    val newDirection: Direction,
)