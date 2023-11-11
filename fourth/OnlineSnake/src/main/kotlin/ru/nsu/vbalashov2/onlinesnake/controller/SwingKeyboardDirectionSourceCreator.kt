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
    private val directionSource = SwingKeyboardDirectionSource()
    override fun createDirectionSource(initDirection: Direction): DirectionSource {
        directionSource.initDirection = initDirection
        directionSource.direction = initDirection
        return directionSource
    }
}
