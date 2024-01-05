package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import ru.nsu.vbalashov2.onlinesnake.dto.GameConfig


fun interface ValidationSuccessListener {
    fun validationSuccess(gameConfig: GameConfig)
}