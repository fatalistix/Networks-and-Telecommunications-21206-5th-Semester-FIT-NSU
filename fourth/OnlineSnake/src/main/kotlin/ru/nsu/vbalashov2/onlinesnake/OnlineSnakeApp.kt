package ru.nsu.vbalashov2.onlinesnake

import kotlinx.coroutines.runBlocking
import ru.nsu.vbalashov2.onlinesnake.controller.Controller

fun main() =
    runBlocking {
        System.setProperty("sun.java2d.opengl", "true")
        Controller().start()
    }
