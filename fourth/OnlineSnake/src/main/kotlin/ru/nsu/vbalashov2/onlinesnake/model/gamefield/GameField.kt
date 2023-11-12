package ru.nsu.vbalashov2.onlinesnake.model.gamefield

import ru.nsu.vbalashov2.onlinesnake.model.Direction
import ru.nsu.vbalashov2.onlinesnake.model.DirectionSource
import ru.nsu.vbalashov2.onlinesnake.model.DirectionSourceCreator
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

class GameField(private val fieldWidth: Int, private val fieldHeight: Int, private val foodStatic: Int) {
    private object FieldConstraints {
        const val MIN_FIELD_WIDTH: Int = 10
        const val MAX_FIELD_WIDTH: Int = 100

        const val MIN_FIELD_HEIGHT: Int = 10
        const val MAX_FIELD_HEIGHT: Int = 100

        const val MIN_FOOD_STATIC: Int = 0
        const val MAX_FOOD_STATIC: Int = 100
    }
    init {
        if (fieldWidth < FieldConstraints.MIN_FIELD_WIDTH || FieldConstraints.MAX_FIELD_WIDTH < fieldWidth) {
            throw IllegalArgumentException("expected: ${FieldConstraints.MIN_FIELD_WIDTH} < field width < " +
                   "${FieldConstraints.MAX_FIELD_WIDTH}, but got $fieldWidth")
        }
        if (fieldHeight < FieldConstraints.MIN_FIELD_HEIGHT || FieldConstraints.MAX_FIELD_HEIGHT < fieldHeight) {
            throw IllegalArgumentException("expected: ${FieldConstraints.MIN_FIELD_HEIGHT} < field height < " +
                    "${FieldConstraints.MAX_FIELD_HEIGHT}, but got $fieldWidth")
        }
        if (foodStatic < FieldConstraints.MIN_FOOD_STATIC || FieldConstraints.MAX_FOOD_STATIC < foodStatic) {
            throw IllegalArgumentException("expected: ${FieldConstraints.MIN_FOOD_STATIC} < food static < " +
                    "${FieldConstraints.MAX_FOOD_STATIC}, but got $foodStatic")
        }
    }

    private val fieldArraySize = fieldWidth * fieldHeight
    private val snakeGameField = IntArray(fieldArraySize)
    private val snakesMapper = SnakesMapper()
    private val foodSet: MutableSet<Int> = mutableSetOf()

    init{
        placeMissingFood()
    }

    private fun placeMissingFood() {
        cleanFoodSet()
        val freeSpace = countFreeSpace()

        for (i in 0..<min((foodStatic+snakesMapper.numOfSnakes()), freeSpace) - foodSet.size) {
            placeFood(freeSpace - i)
        }
    }

    private fun cleanFoodSet() = foodSet.removeIf { snakeGameField[it] != -1 }
    private fun countFreeSpace(): Int = snakeGameField.size - foodSet.size - snakesMapper.getAllSnakes().sumOf { it.snake.coords.size }

    private fun placeFood(freeSpace: Int) {
        if (freeSpace <= 0) {
            throw IllegalArgumentException("freeSpace cannot be less or equal to 0 (got $freeSpace)")
        }
        val randomPos = (0..<freeSpace).random()
        var n = -1
        var i = 0
        while (n < randomPos) {
            if (snakeGameField[i++] == 0) {
                ++n
            }
        }
        snakeGameField[i-1] = -1
        foodSet.add(i-1)
    }

    private fun find5x5FreeRegion(): Pair<Int, Boolean> {
        for (i in 0..<this.fieldHeight) {
            for (j in 0..<this.fieldWidth) {
                if (isCorrectRegion(i, j)) {
                    return Pair(((i + 2) % this.fieldHeight) * this.fieldWidth + ((j + 2) % this.fieldWidth), true)
                }
            }
        }
        return Pair(0, false)
    }

    private fun isCorrectRegion(leftTopX: Int, leftTopY: Int) : Boolean {
        for (i in leftTopX..<leftTopX + 5) {
            for (j in leftTopY..<leftTopY + 5) {
                val ceil = this.snakeGameField[correctModule(i, this.fieldHeight) * this.fieldWidth + correctModule(j, this.fieldWidth)]
                if (ceil != 0 && ceil != -1) {
                    return false
                }
            }
        }
        val xc = this.snakeGameField[correctModule(leftTopX + 2, this.fieldHeight) * this.fieldWidth + correctModule(leftTopY + 2, this.fieldWidth)]
        val xl = this.snakeGameField[correctModule(leftTopX + 2, this.fieldHeight) * this.fieldWidth + correctModule(leftTopY + 1, this.fieldWidth)]
        val xd = this.snakeGameField[correctModule(leftTopX + 3, this.fieldHeight) * this.fieldWidth + correctModule(leftTopY + 2, this.fieldWidth)]
        val xr = this.snakeGameField[correctModule(leftTopX + 2, this.fieldHeight) * this.fieldWidth + correctModule(leftTopY + 3, this.fieldWidth)]
        val xu = this.snakeGameField[correctModule(leftTopX + 1, this.fieldHeight) * this.fieldWidth + correctModule(leftTopY + 2, this.fieldWidth)]
        return (xc == 0) && (xl == 0 || xd == 0 || xr == 0 || xu == 0)
    }

    private fun fieldArrayIndexToPosition(index: Int): Position {
        return Position(index % this.fieldWidth, index / this.fieldWidth)
    }

    private fun positionToFieldArrayIndex(position: Position): Int = position.y * this.fieldWidth + position.x

    fun createSnake(directionSourceCreator: DirectionSourceCreator): FieldKey {
        val result = find5x5FreeRegion()
        if (!result.second) {
            throw NoFreeSpaceException()
        }
        val headPosition = fieldArrayIndexToPosition(result.first)
        val context = getSurroundingContext(headPosition)
        val possibleDirections = Direction.entries.toTypedArray().filter { context[it] == 0 }

//        val tailDirection = Direction.getRandomDirection()
        val tailDirection = possibleDirections.random()
        val tailPosition = when (tailDirection) {
            Direction.LEFT -> getLefterPosition(headPosition)
            Direction.DOWN -> getDownerPosition(headPosition)
            Direction.RIGHT -> getRighterPosition(headPosition)
            Direction.UP -> getUpperPosition(headPosition)
        }
        val snake = Snake(
            headPosition,
            tailPosition,
            this.fieldHeight,
            this.fieldWidth,
            directionSourceCreator.createDirectionSource(tailDirection.getOppositeDirection())
        )

        val mapResult = snakesMapper.addSnake(snake)

        placeSnakeOnField(snake, mapResult.first)

        placeMissingFood()

        return mapResult.second
    }

    private fun getLefterPosition(pos: Position): Position =
        Position(correctModule(pos.x - 1, this.fieldWidth), pos.y)
    private fun getDownerPosition(pos: Position): Position =
        Position(pos.x, correctModule(pos.y + 1, this.fieldHeight))
    private fun getRighterPosition(pos: Position): Position =
        Position(correctModule(pos.x + 1, this.fieldWidth), pos.y)
    private fun getUpperPosition(pos: Position): Position =
        Position(pos.x, correctModule(pos.y - 1, this.fieldHeight))

    private fun placeSnakeOnField(snake: Snake, snakeNumber: Int) =
        snake.coords.forEach { this.snakeGameField[positionToFieldArrayIndex(it)] = snakeNumber }

    private fun updateSnakeTailOnField(snake: Snake, snakeNumber: Int) {
        if (snake.tail != snake.oldTail) {
            this.snakeGameField[positionToFieldArrayIndex(snake.tail)] = snakeNumber
            this.snakeGameField[positionToFieldArrayIndex(snake.oldTail)] = 0
        }
    }

    private fun updateSnakeHeadOnField(snake: Snake, snakeNumber: Int) =
        snakeNumber.also { this.snakeGameField[positionToFieldArrayIndex(snake.head)] = it }

    private fun removeSnakeOnField(snake: Snake) =
        snake.coords.forEach { this.snakeGameField[positionToFieldArrayIndex(it)] = 0 }

    fun updateField(): Pair<List<List<Position>>, List<Position>> {
        val snakesList = snakesMapper.getAllSnakes()
        snakesList.forEach { snakeWithInfo -> snakeWithInfo.snake.move(getSurroundingContext(snakeWithInfo.snake.head)) }
        val snakesListForRemoval = snakesList.filter { snakeWithInfo ->
            val headCache = this.snakeGameField[positionToFieldArrayIndex(snakeWithInfo.snake.head)]
            if (headCache == 0 || headCache == -1) {
                val collisionSnake = snakesList.find { collisionSnakeWithInfo ->
                    collisionSnakeWithInfo.snake.head == snakeWithInfo.snake.head &&
                            collisionSnakeWithInfo.fieldKey != snakeWithInfo.fieldKey
                }
                collisionSnake != null
            } else {
                val collisionSnake = snakesList.find { collisionSnakeWithInfo ->
                    collisionSnakeWithInfo.fieldIndex == headCache
                }!!
                collisionSnake.snake.tail != snakeWithInfo.snake.head
            }
        }
        snakesMapper.getAllSnakes().forEach { snakeWithInfo ->
            updateSnakeTailOnField(snakeWithInfo.snake, snakeWithInfo.fieldIndex)
            updateSnakeHeadOnField(snakeWithInfo.snake, snakeWithInfo.fieldIndex)
        }
        snakesListForRemoval.forEach { snakeWithInfo ->
            this.removeSnakeOnField(snakeWithInfo.snake)
            snakesMapper.removeSnake(snakeWithInfo.fieldKey)
        }
        placeMissingFood()
//        return Pair(snakesListForRemoval.map { snakeWithInfo -> snakeWithInfo.fieldKey }, this.snakeGameField.copyOf())
        return Pair(snakesList.map { it.snake.keyPoints.toList() }, foodSet.toList().map { fieldArrayIndexToPosition(it) })
    }

    private fun getSurroundingContext(position: Position): Context {
        val lefterPosition = getLefterPosition(position)
        val downerPosition = getDownerPosition(position)
        val righterPosition = getRighterPosition(position)
        val upperPosition = getUpperPosition(position)
        return Context(
            left = snakeGameField[positionToFieldArrayIndex(lefterPosition)],
            down = snakeGameField[positionToFieldArrayIndex(downerPosition)],
            right = snakeGameField[positionToFieldArrayIndex(righterPosition)],
            up = snakeGameField[positionToFieldArrayIndex(upperPosition)]
        )
    }

    fun removeSnake(fieldKey: FieldKey) {
        val result = this.snakesMapper.removeSnake(fieldKey)!!
        this.removeSnakeOnField(result.first)
    }
}

private data class Context(
    val left: Int,
    val down: Int,
    val right: Int,
    val up: Int
) {
    operator fun get(direction: Direction): Int = when (direction) {
        Direction.LEFT -> left
        Direction.DOWN -> down
        Direction.RIGHT -> right
        Direction.UP -> up
    }
}

private data class SnakeWithInfo(
    val snake: Snake,
    val fieldKey: FieldKey,
    val fieldIndex: Int
)

private class SnakesMapper {
    private val releasedIndexesSet = HashSet<Int>()
    private var maxUnusedIndex = 1
    private val fieldKeyToSnakeWithId = HashMap<FieldKey, Pair<Snake, Int>>()

    fun addSnake(snake: Snake): Pair<Int, FieldKey> {
        val fieldKey = FieldKey()
        return if (releasedIndexesSet.isEmpty()) {
            val currentSnakeIndex = maxUnusedIndex++
            fieldKeyToSnakeWithId[fieldKey] = Pair(snake, currentSnakeIndex)
            Pair(currentSnakeIndex, fieldKey)
        } else {
            val iter = releasedIndexesSet.iterator()
            val currentSnakeIndex = iter.next()
            iter.remove()
            fieldKeyToSnakeWithId[fieldKey] = Pair(snake, currentSnakeIndex)
            Pair(currentSnakeIndex, fieldKey)
        }
    }

    fun removeSnake(fieldKey: FieldKey): Pair<Snake, Int>? {
        return if (fieldKey in fieldKeyToSnakeWithId) {
            val currentSnakeWithIndex = fieldKeyToSnakeWithId.remove(fieldKey)!!
            releasedIndexesSet.add(currentSnakeWithIndex.second)
            currentSnakeWithIndex
        } else {
            null
        }
    }

    fun getAllSnakes(): List<SnakeWithInfo> = fieldKeyToSnakeWithId.entries
        .map{ entry ->
            SnakeWithInfo(fieldKey = entry.key, fieldIndex = entry.value.second, snake = entry.value.first)
        }

//    fun getAllSnakesSize(): Int = fieldKeyToSnakeWithId.values.sumOf { it.first.coords.size }
    fun numOfSnakes(): Int = fieldKeyToSnakeWithId.size
}

private fun correctModule(a: Int, b: Int): Int {
    return (b + (a % b)) % b
}



private class Snake(
    head: Position,
    tail: Position,
    private val fieldHeight: Int,
    private val fieldWidth: Int,
    private val directionSource: DirectionSource
) {
    val coords = ArrayDeque<Position>(2)
    val keyPoints = ArrayDeque<Position>(2)
    private var prevDirection = directionSource.initDirection
    val head: Position
        get() = coords.first()
    val tail: Position
        get() = coords.last()
    var oldTail = tail
        private set
    init {
        coords.addLast(head)
        coords.addLast(tail)
    }

    init {
        keyPoints.addLast(head)
        keyPoints.addLast(when (directionSource.initDirection) {
            Direction.LEFT -> Position(1, 0)
            Direction.DOWN -> Position(0, -1)
            Direction.RIGHT -> Position(-1, 0)
            Direction.UP -> Position(0, 1)
        })
//        println("at init: $keyPoints")
    }

    fun move(context: Context) {
        val head = coords[0]
        oldTail = tail
        val direction = if (directionSource.direction == prevDirection.getOppositeDirection())
            prevDirection
        else
            directionSource.direction

        when (direction) {
            Direction.LEFT -> {
                if (head.x == 0) {
                    coords.addFirst(Position(fieldWidth - 1, head.y))
                } else {
                    coords.addFirst(Position(head.x - 1, head.y))
                }

                if (direction == prevDirection) {
                    keyPoints[1] = Position(keyPoints[1].x + 1, keyPoints[1].y)
                    if (keyPoints.first().x == 0) {
                        keyPoints[0] = Position(fieldWidth - 1, keyPoints.first().y)
                    } else {
                        keyPoints[0] = Position(keyPoints.first().x - 1, keyPoints.first().y)
                    }
                } else {
                    if (keyPoints.first().x == 0) {
                        keyPoints.addFirst(Position(fieldWidth - 1, keyPoints.first().y))
                    } else {
                        keyPoints.addFirst(Position(keyPoints.first().x - 1, keyPoints.first().y))
                    }
                    keyPoints[1] = Position(1, 0)
                }
            }
            Direction.DOWN -> {
                if (head.y == fieldHeight - 1) {
                    coords.addFirst(Position(head.x, 0))
                } else {
                    coords.addFirst(Position(head.x, head.y + 1))
                }

                if (direction == prevDirection) {
                    keyPoints[1] = Position(keyPoints[1].x, keyPoints[1].y - 1)
                    if (keyPoints.first().y == fieldHeight - 1) {
                        keyPoints[0] = Position(keyPoints.first().x, 0)
                    } else {
                        keyPoints[0] = Position(keyPoints.first().x, keyPoints.first().y + 1)
                    }
                } else {
                    if (keyPoints.first().y == fieldHeight - 1) {
                        keyPoints.addFirst(Position(keyPoints.first().x, 0))
                    } else {
                        keyPoints.addFirst(Position(keyPoints.first().x, keyPoints.first().y + 1))
                    }
                    keyPoints[1] = Position(0, -1)
                }
            }
            Direction.RIGHT -> {
                if (head.x == fieldWidth - 1) {
                    coords.addFirst(Position(0, head.y))
                } else {
                    coords.addFirst(Position(head.x + 1, head.y))
                }

                if (direction == prevDirection) {
                    keyPoints[1] = Position(keyPoints[1].x - 1, keyPoints[1].y)
                    if (keyPoints.first().x == fieldWidth - 1) {
                        keyPoints[0] = Position(0, keyPoints.first().y)
                    } else {
                        keyPoints[0] = Position(keyPoints.first().x + 1, keyPoints.first().y)
                    }
                } else {
                    if (keyPoints.first().x == fieldWidth - 1) {
                        keyPoints.addFirst(Position(0, keyPoints.first().y))
                    } else {
                        keyPoints.addFirst(Position(keyPoints.first().x + 1, keyPoints.first().y))
                    }
                    keyPoints[1] = Position(-1, 0)
                }
            }
            Direction.UP -> {
                if (head.y == 0) {
                    coords.addFirst(Position(head.x, fieldHeight - 1))
                } else {
                    coords.addFirst(Position(head.x, head.y - 1))
                }

                if (direction == prevDirection) {
                    keyPoints[1] = Position(keyPoints[1].x, keyPoints[1].y + 1)
                    if (keyPoints.first().y == 0) {
                        keyPoints[0] = Position(keyPoints.first().x, fieldHeight - 1)
                    } else {
                        keyPoints[0] = Position(keyPoints.first().x, keyPoints.first().y - 1)
                    }
                } else {
                    if (keyPoints.first().y == 0) {
                        keyPoints.addFirst(Position(keyPoints.first().x, fieldHeight - 1))
                    } else {
                        keyPoints.addFirst(Position(keyPoints.first().x, keyPoints.first().y - 1))
                    }
                    keyPoints[1] = Position(0, 1)
                }
            }
        }
        if (context[direction] != -1) {
            coords.removeLast()
//            println("at != -1: ${keyPoints}")
//            if (keyPoints.size == 2) {
//                keyPoints[1] = when (direction) {
//                    Direction.LEFT -> Position(keyPoints.last().x - 1, keyPoints.last().y)
//                    Direction.DOWN -> Position(keyPoints.last().x, keyPoints.last().y + 1)
//                    Direction.RIGHT ->  Position(keyPoints.last().x + 1, keyPoints.last().y)
//                    Direction.UP -> Position(keyPoints.last().x, keyPoints.last().y - 1)
//                }
//            } else {
            val lastX = keyPoints.last().x
            val lastY = keyPoints.last().y
            if (lastX == 0) {
                if (lastY.absoluteValue == 1) {
                    keyPoints.removeLast()
                } else {
                    keyPoints[keyPoints.size - 1] = Position(0, (lastY.absoluteValue - 1) * lastY.sign)
                }
            } else {
                if (lastX.absoluteValue == 1) {
                    keyPoints.removeLast()
                } else {
                    keyPoints[keyPoints.size - 1] = Position((lastX.absoluteValue - 1) * lastX.sign, 0)
                }
            }

//            println("after != -1: ${keyPoints}")
//            }
//            if (diffX == 0) {
//                if (diffY.absoluteValue == 1) {
//                    keyPoints.removeLast()
//                } else {
//                    keyPoints[keyPoints.size - 1] = Position(0, (diffY.absoluteValue - 1) * diffY.sign)
//                }
//            } else {
//                println("DIFFX: $keyPoints")
//                if (diffX.absoluteValue == 1) {
//                    keyPoints.removeLast()
//                } else {
//                    keyPoints[keyPoints.size - 1] = Position((diffX.absoluteValue - 1) * diffX.sign, 0)
//                }
//            }
//            keyPoints.removeLast()
//            if (coords.last() != keyPoints.last()) {
//                keyPoints.addLast(coords.last())
//
        }
        prevDirection = direction
    }
}