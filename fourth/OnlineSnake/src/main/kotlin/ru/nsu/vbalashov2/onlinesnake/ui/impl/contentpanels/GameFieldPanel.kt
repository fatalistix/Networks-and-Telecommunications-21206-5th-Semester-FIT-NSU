package ru.nsu.vbalashov2.onlinesnake.ui.contentpanels

import java.awt.*
import javax.swing.JPanel
import kotlin.math.roundToInt

class GameFieldPanel : JPanel() {
    private var field = IntArray(1) { 0 }
    private var fieldWidth: Int = 1
    private var fieldHeight: Int = 1
    private val snakeColors = arrayOf(Color.WHITE, Color.BLACK, Color.BLUE, Color.CYAN, Color.RED, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY)
    private val foodColor = Color.GREEN
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

    override fun paint(g: Graphics?) {
        super.paint(g)
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
        for (i in 0..<fieldHeight) {
            for (j in 0..<fieldWidth) {
                if (field[i * fieldWidth + j] == -1) {
                    g?.color = foodColor
                } else {
                    g?.color = snakeColors[field[i * fieldWidth + j]]
                }
                g?.fillRect(offset.width + (j * brickWidth).roundToInt(), offset.height + (i * brickHeight).roundToInt(), brickWidth.roundToInt(), brickHeight.roundToInt())
            }
        }
    }

    fun updateField(newField: IntArray, newFieldWidth: Int, newFieldHeight: Int) {
        this.fieldWidth = newFieldWidth
        this.fieldHeight = newFieldHeight
        this.field = newField
        repaint()
    }
}