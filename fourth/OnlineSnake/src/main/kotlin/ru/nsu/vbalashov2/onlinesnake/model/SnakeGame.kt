package ru.nsu.vbalashov2.onlinesnake.model

import ru.nsu.vbalashov2.onlinesnake.model.gamefield.FieldKey
import ru.nsu.vbalashov2.onlinesnake.model.gamefield.GameField
import ru.nsu.vbalashov2.onlinesnake.model.gamefield.Position
import kotlin.concurrent.timer

class SnakeGame(
    width: Int,
    height: Int,
    foodStatic: Int,
    stateDelayMs: Int,
//    onFieldUpdate: (snakeKeysForRemoval: List<SnakeKey>, fieldArray: IntArray) -> Unit
    onFieldUpdate: (snakesKeyPointsList: List<List<Position>>, foodList: List<Position>) -> Unit
) {
    private object GameConstraints {
        const val MIN_STATE_DELAY_MS = 100
        const val MAX_STATE_DELAY_MS = 3000
    }

    init {
        if (stateDelayMs < GameConstraints.MIN_STATE_DELAY_MS || GameConstraints.MAX_STATE_DELAY_MS < stateDelayMs) {
            throw IllegalArgumentException("state delay ms must be from " +
                    "${GameConstraints.MIN_STATE_DELAY_MS} to " +
                    "${GameConstraints.MAX_STATE_DELAY_MS}")
        }
    }

    private val gameField = GameField(fieldWidth = width, fieldHeight = height, foodStatic = foodStatic)
    private val keysRelations = KeysRelations()

    private val updateTimer = timer(initialDelay=0, period=stateDelayMs.toLong()) {
//        val result: Pair<List<FieldKey>, IntArray>
        val result: Pair<List<List<Position>>, List<Position>>
        synchronized(gameField) {
            result = gameField.updateField()
        }
//        onFieldUpdate(result.first.map { keysRelations[it]!! }, result.second)
        onFieldUpdate(result.first, result.second)
    }

    fun createSnake(directionSourceCreator: DirectionSourceCreator): SnakeKey {
        synchronized(gameField) {
            val fieldKey = gameField.createSnake(directionSourceCreator)
            val snakeKey = SnakeKey()
            this.keysRelations.putRelation(snakeKey, fieldKey)
            return snakeKey
        }
    }

    fun removeSnake(snakeKey: SnakeKey) {
        synchronized(gameField) {
            if (snakeKey !in keysRelations) {
                return
            }
            gameField.removeSnake(keysRelations[snakeKey]!!)
            keysRelations.removeRelation(snakeKey, keysRelations[snakeKey]!!)
        }
    }

    fun close() {
        updateTimer.cancel()
    }
}

private class KeysRelations {
    private val snakeKeyToFieldKey = HashMap<SnakeKey, FieldKey>()
    private val fieldKeyToSnakeKey = HashMap<FieldKey, SnakeKey>()

    fun putRelation(snakeKey: SnakeKey, fieldKey: FieldKey) {
        snakeKeyToFieldKey[snakeKey] = fieldKey
        fieldKeyToSnakeKey[fieldKey] = snakeKey
    }

    fun removeRelation(snakeKey: SnakeKey, fieldKey: FieldKey) {
        snakeKeyToFieldKey.remove(snakeKey)
        fieldKeyToSnakeKey.remove(fieldKey)
    }

    operator fun get(snakeKey: SnakeKey): FieldKey? = snakeKeyToFieldKey[snakeKey]
    operator fun get(fieldKey: FieldKey): SnakeKey? = fieldKeyToSnakeKey[fieldKey]

    operator fun contains(fieldKey: FieldKey): Boolean = fieldKey in fieldKeyToSnakeKey
    operator fun contains(snakeKey: SnakeKey): Boolean = snakeKey in snakeKeyToFieldKey
}
