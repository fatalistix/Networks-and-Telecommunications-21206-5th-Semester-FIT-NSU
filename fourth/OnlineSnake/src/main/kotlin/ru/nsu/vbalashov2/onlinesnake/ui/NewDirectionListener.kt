package ru.nsu.vbalashov2.onlinesnake.ui

import ru.nsu.vbalashov2.onlinesnake.dto.Direction

fun interface NewDirectionListener {
    fun newDirection(direction: Direction)
}