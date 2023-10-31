package ru.nsu.vbalashov2.onlinesnake.ui

import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import kotlin.math.roundToInt

class GameFieldPanel(private val fieldWidth: Int, private val fieldHeight: Int) : JPanel() {
    val field = IntArray(fieldWidth * fieldHeight) { _ -> 0 }
    private val snakeColors = arrayOf(Color.WHITE, Color.BLACK, Color.BLUE, Color.CYAN, Color.RED, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY)
    private val foodColor = Color.GREEN
    private inner class ResizeListener : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent?) {
            super.componentResized(e)
            size = if (height.toDouble() * fieldWidth > width.toDouble() * fieldHeight) {
                Dimension(width, (width.toDouble() / fieldWidth * fieldHeight).roundToInt())
            } else {
                Dimension((height.toDouble() / fieldHeight * fieldWidth).roundToInt(), height)
            }
        }
    }
    init {
        this.addComponentListener(ResizeListener())
        field[0] = 1
        field[5] = 3
        field[6] = 6
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        val brickWidth = this.width.toDouble() / this.fieldWidth
        val brickHeight = this.height.toDouble() / this.fieldHeight
        for (i in 0..<fieldHeight) {
            for (j in 0..<fieldWidth) {
                if (field[i * fieldWidth + j] == -1) {
                    g?.color = foodColor
                } else {
                    g?.color = snakeColors[field[i * fieldWidth + j]]
                }
                g?.fillRect((j * brickWidth).roundToInt(), (i * brickHeight).roundToInt(), brickWidth.roundToInt(), brickHeight.roundToInt())
            }
        }
    }

    fun updateField(newField: IntArray) {
        assert(newField.size == field.size)
        for (i in newField.indices) {
            field[i] = newField[i]
        }
        repaint()
    }
}