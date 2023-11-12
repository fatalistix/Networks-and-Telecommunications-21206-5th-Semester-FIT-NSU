package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint
import java.awt.*
import javax.swing.JPanel
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sign

class GameFieldPanel : JPanel() {
    private var field = IntArray(1) { 0 }
    private var fieldWidth: Int = 1
    private var fieldHeight: Int = 1
    private val snakeColors = arrayOf(Color.BLACK, Color.BLUE, Color.CYAN, Color.RED, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY)
    private val foodColor = Color.GREEN
    private var snakesKeyPointsList: List<List<KeyPoint>> = listOf()
    private var foodList: List<KeyPoint> = listOf()
//    private inner class ResizeListener : ComponentAdapter() {
//        override fun componentResized(e: ComponentEvent?) {
//            super.componentResized(e)
//            size = if (height.toDouble() * fieldWidth > width.toDouble() * fieldHeight) {
//                Dimension(width, (width.toDouble() / fieldWidth * fieldHeight).roundToInt())
//            } else {
//                Dimension((height.toDouble() / fieldHeight * fieldWidth).roundToInt(), height)
//            }
//        }
//    }
//    init {
//        this.addComponentListener(ResizeListener())
//    }

//    override fun paint(g: Graphics?) {
//        super.paint(g)
//        val centerWidth: Boolean
//        val fieldSizeDimension = if (height.toDouble() * fieldWidth > width.toDouble() * fieldHeight) {
//            centerWidth = false
//            Dimension(width, (width.toDouble() / fieldWidth * fieldHeight).roundToInt())
//        } else {
//            centerWidth = true
//            Dimension((height.toDouble() / fieldHeight * fieldWidth).roundToInt(), height)
//        }
//
//        val offset = if (centerWidth) {
//            Dimension((width - fieldSizeDimension.width) / 2, 0)
//        } else {
//            Dimension(0, (height - fieldSizeDimension.height) / 2)
//        }
//
//        val brickWidth = fieldSizeDimension.width.toDouble() / this.fieldWidth
//        val brickHeight = fieldSizeDimension.height.toDouble() / this.fieldHeight
//        for (i in 0..<fieldHeight) {
//            for (j in 0..<fieldWidth) {
//                if (field[i * fieldWidth + j] == -1) {
//                    g?.color = foodColor
//                } else {
//                    g?.color = snakeColors[field[i * fieldWidth + j]]
//                }
//                g?.fillRect(offset.width + (j * brickWidth).roundToInt(), offset.height + (i * brickHeight).roundToInt(), brickWidth.roundToInt(), brickHeight.roundToInt())
//            }
//        }
//    }

    override fun paint(g : Graphics?) {
        val centerWidth: Boolean
        val fieldSizeDimension = if (height.toDouble() * fieldWidth > width.toDouble() * fieldHeight) {
            centerWidth = false
            Dimension(width, (width.toDouble() / fieldWidth * fieldHeight).roundToInt())
        } else {
            centerWidth = true
            Dimension((height.toDouble() / fieldHeight * fieldWidth).roundToInt(), height)
        }

        val offset = if (centerWidth) {
            Dimension((width - fieldSizeDimension.width) / 2, 0)
        } else {
            Dimension(0, (height - fieldSizeDimension.height) / 2)
        }

        val brickWidth = fieldSizeDimension.width.toDouble() / this.fieldWidth
        val brickHeight = fieldSizeDimension.height.toDouble() / this.fieldHeight
        val brickWidthInt = brickWidth.roundToInt()
        val brickHeightInt = brickHeight.roundToInt()
        g?.color = Color.WHITE
        g?.fillRect(offset.width, offset.height, fieldSizeDimension.width, fieldSizeDimension.height)

        g?.color = foodColor
//        println("<==>")
        for (foodPoint in foodList) {
//            println("$foodPoint ${offset.width}, ${offset.height}, ${offset.width + (foodPoint.x * brickWidth).roundToInt()}, ${offset.height + (foodPoint.y + brickHeight).roundToInt()}")
            g?.fillRect(
                offset.width + (foodPoint.x * brickWidth).roundToInt(),
                offset.height + (foodPoint.y * brickHeight).roundToInt(),
                brickWidthInt,
                brickHeightInt,
            )
        }

        for (snakeIndex in snakesKeyPointsList.indices) {
            val snake = snakesKeyPointsList[snakeIndex]
            var cx = snake.first().x
            var cy = snake.first().y
            g?.color = snakeColors[snakeIndex]
            g?.fillRect(
                offset.width + (cx * brickWidth).roundToInt(),
                offset.height + (cy * brickHeight).roundToInt(),
                brickWidthInt,
                brickHeightInt,
            )
            for (nextCoordIndex in 1..<snake.size) {
                if (snake[nextCoordIndex].x == 0) {
                    val yCache = snake[nextCoordIndex].y
                    val yCacheAbsolute = yCache.absoluteValue
                    val yCacheSign = yCache.sign
                    for (j in 0..<yCacheAbsolute) {
                        cy += yCacheSign
                        cy = when (cy) {
                            -1 -> fieldHeight - 1
                            fieldHeight -> 0
                            else -> cy
                        }
                        g?.fillRect(
                            offset.width + (cx * brickWidth).roundToInt(),
                            offset.height + (cy * brickHeight).roundToInt(),
                            brickWidthInt,
                            brickHeightInt,
                        )
                    }
                } else {
                    val xCache = snake[nextCoordIndex].x
                    val xCacheAbsolute = xCache.absoluteValue
                    val xCacheSign = xCache.sign
                    for (i in 0..<xCacheAbsolute) {
                        cx += xCacheSign
                        cx = when (cx) {
                            -1 -> fieldWidth - 1
                            fieldWidth -> 0
                            else -> cx
                        }
                        g?.fillRect(
                            offset.width + (cx * brickWidth).roundToInt(),
                            offset.height + (cy * brickHeight).roundToInt(),
                            brickWidthInt,
                            brickHeightInt,
                        )
                    }
                }
            }
        }
    }

//    fun updateField(newField: IntArray, newFieldWidth: Int, newFieldHeight: Int) {
//        this.fieldWidth = newFieldWidth
//        this.fieldHeight = newFieldHeight
//        this.field = newField
//        repaint()
//    }

    fun updateField(snakesKeyPointsList: List<List<KeyPoint>>, foodList: List<KeyPoint>, fieldWidth: Int, fieldHeight: Int) {
        this.fieldWidth = fieldWidth
        this.fieldHeight = fieldHeight
        this.snakesKeyPointsList = snakesKeyPointsList
        this.foodList = foodList
        repaint()
    }
}