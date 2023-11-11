package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.model.DirectionSource
import ru.nsu.vbalashov2.onlinesnake.model.Direction
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class SwingKeyboardDirectionSource : DirectionSource /*, KeyAdapter() */ {
    override var initDirection: Direction = Direction.DOWN
    override var direction: Direction = Direction.DOWN

    init {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher {
                val keyCode = it!!.keyCode
                direction = when (keyCode) {
                    KeyEvent.VK_LEFT, KeyEvent.VK_A -> Direction.LEFT
                    KeyEvent.VK_DOWN, KeyEvent.VK_S -> Direction.DOWN
                    KeyEvent.VK_RIGHT, KeyEvent.VK_D -> Direction.RIGHT
                    KeyEvent.VK_UP, KeyEvent.VK_W -> Direction.UP
                    else -> direction
                }
                false
            }
    }
//
//    override fun keyPressed(e: KeyEvent?) {
//        val keyCode = e!!.keyCode
//        _direction = when (keyCode) {
//            KeyEvent.VK_LEFT, KeyEvent.VK_A -> Direction.LEFT
//            KeyEvent.VK_DOWN, KeyEvent.VK_S -> Direction.DOWN
//            KeyEvent.VK_RIGHT, KeyEvent.VK_D -> Direction.RIGHT
//            KeyEvent.VK_UP, KeyEvent.VK_W -> Direction.UP
//            else -> _direction
//        }
//        println(_direction)
//    }
}