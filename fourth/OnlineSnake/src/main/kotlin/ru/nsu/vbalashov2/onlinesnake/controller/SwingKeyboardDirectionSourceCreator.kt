package ru.nsu.vbalashov2.onlinesnake.controller

import ru.nsu.vbalashov2.onlinesnake.model.DirectionSource
import ru.nsu.vbalashov2.onlinesnake.model.DirectionSourceCreator
import ru.nsu.vbalashov2.onlinesnake.model.Direction
import java.awt.Component
import javax.swing.JComponent
import javax.swing.JFrame

class SwingKeyboardDirectionSourceCreator/*(private val component: Component)*/: DirectionSourceCreator {
//    override fun createDirectionSource(initDirection: Direction): DirectionSource {
//        val directionSource = SwingKeyboardDirectionSource(initDirection)
//        component.addKeyListener(directionSource)
//        return directionSource
//    }
    override fun createDirectionSource(initDirection: Direction): DirectionSource = SwingKeyboardDirectionSource(initDirection)
}
