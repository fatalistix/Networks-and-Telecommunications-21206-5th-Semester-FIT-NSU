package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Direction

fun interface DirectionChangedListener {
    fun directionChanged(direction: Direction)
}