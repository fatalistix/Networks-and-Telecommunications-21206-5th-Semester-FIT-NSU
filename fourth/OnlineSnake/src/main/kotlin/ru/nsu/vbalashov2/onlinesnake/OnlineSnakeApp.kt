package ru.nsu.vbalashov2.onlinesnake

import ru.nsu.vbalashov2.onlinesnake.controller.Controller
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

class OnlineSnakeApp : JFrame() {
    private val mainFrameName = "OnlineSnake"

    init {
        this.title = mainFrameName
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.pack()
        this.setLocationRelativeTo(null)
        this.size = Dimension(200, 200)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val frame = OnlineSnakeApp()
        Controller(frame)
        frame.isVisible = true
    }
}