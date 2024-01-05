package ru.nsu.vbalashov2.onlinesnake.ui

import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameDto
import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameKey
import ru.nsu.vbalashov2.onlinesnake.ui.dto.UpdateGameDto

typealias WidthValidationRule = IntValidationRule
typealias HeightValidationRule = IntValidationRule
typealias FoodStaticValidationRule = IntValidationRule
typealias StateDelayMsValidationRule = IntValidationRule

interface GameUI {
    fun start()
    fun updateField(updateGameDto: UpdateGameDto)
    fun addNewGameListener(listener: NewGameListener)
    fun addExitListener(listener: ExitListener)
    fun addWidthValidationRule(validationRule: WidthValidationRule)
    fun addHeightValidationRule(validationRule: HeightValidationRule)
    fun addFoodStaticValidationRule(validationRule: FoodStaticValidationRule)
    fun addStateDelayMsValidationRule(validationRule: StateDelayMsValidationRule)
    fun addApplicationCloseListener(listener: ApplicationCloseListener)
    fun addAvailableGame(availableGameDto: AvailableGameDto, selectedListener: AvailableGameSelectedListener) : AvailableGameKey
    fun removeAvailableGame(key: AvailableGameKey)
    fun updateAvailableGame(availableGameDto: AvailableGameDto, key: AvailableGameKey)
    fun addNewDirectionListener(listener: NewDirectionListener)
    fun showError(title: String, message: String)
}