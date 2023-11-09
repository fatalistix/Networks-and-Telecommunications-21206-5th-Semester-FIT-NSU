package ru.nsu.vbalashov2.onlinesnake.model

interface DirectionSourceCreator {
    fun createDirectionSource(initDirection: Direction): DirectionSource
}