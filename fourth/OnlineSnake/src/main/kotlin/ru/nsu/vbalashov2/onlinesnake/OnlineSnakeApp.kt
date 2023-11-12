package ru.nsu.vbalashov2.onlinesnake

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.nsu.vbalashov2.onlinesnake.controller.Controller
import javax.swing.SwingUtilities
import javax.swing.UIManager

//fun main() {
//    runBlocking(Dispatchers.Main) {
//        System.setProperty("sun.java2d.opengl", "true")
//        SwingUtilities.invokeLater {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
//            val frame = OnlineSnakeApp()
//            Controller(frame)
//            frame.isVisible = true
//        }
//    }
//}

fun main() {
    runBlocking {
        System.setProperty("sun.java2d.opengl", "true")
        Controller()
    }
}