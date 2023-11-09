package ru.nsu.vbalashov2.onlinesnake.model

import ru.nsu.vbalashov2.onlinesnake.model.gamefield.FieldKey
import ru.nsu.vbalashov2.onlinesnake.model.gamefield.GameField
import kotlin.concurrent.timer

class SnakeGame(
    width: Int,
    height: Int,
    updateIntervalMillis: Long,
    onFieldUpdate: (snakeKeysForRemoval: List<SnakeKey>, fieldArray: IntArray) -> Unit
) {
    private val gameField = GameField(fieldWidth = width, fieldHeight = height, foodStatic = 2)
    private val keysRelations = KeysRelations()

    private val updateTimer = timer(initialDelay=0, period=updateIntervalMillis) {
        val result: Pair<List<FieldKey>, IntArray>
        synchronized(gameField) {
            result = gameField.updateField()
        }
        onFieldUpdate(result.first.map { keysRelations[it]!! }, result.second)
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
