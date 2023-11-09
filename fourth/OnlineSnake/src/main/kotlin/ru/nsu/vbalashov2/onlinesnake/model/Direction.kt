package ru.nsu.vbalashov2.onlinesnake.model

enum class Direction {
    LEFT,
    DOWN,
    RIGHT,
    UP;

    fun getOppositeDirection(): Direction {
        return entries[(this.ordinal + 2) % 4]
    }

    companion object {
        fun getRandomDirection(): Direction {
            return entries[(0..<4).random()]
        }
    }
}