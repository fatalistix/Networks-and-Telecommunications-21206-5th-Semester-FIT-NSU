package ru.nsu.vbalashov2.onlinesnake.ui

import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameInfo
import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint

typealias WidthValidationRule = IntValidationRule
typealias HeightValidationRule = IntValidationRule
typealias FoodStaticValidationRule = IntValidationRule
typealias StateDelayMsValidationRule = IntValidationRule

interface GameUI {
//    fun addAvailableGame(info: AvailableGame)
//    fun removeAvailableGame(gameName: String)
//    fun updateField(field: IntArray, width: Int, height: Int)
    fun start()
    fun updateField(snakesKeyPointsList: List<List<KeyPoint>>, foodList: List<KeyPoint>, width: Int, height: Int)
    fun addNewGameListener(listener: NewGameListener) : Int
    fun addExitListener(listener: ExitListener) : Int
    fun addWidthValidationRule(validationRule: WidthValidationRule) : Int
    fun addHeightValidationRule(validationRule: HeightValidationRule) : Int
    fun addFoodStaticValidationRule(validationRule: FoodStaticValidationRule) : Int
    fun addStateDelayMsValidationRule(validationRule: StateDelayMsValidationRule) : Int
    fun addApplicationCloseListener(listener: ApplicationCloseListener) : Int
    fun addAvailableGame(availableGameInfo: AvailableGameInfo, selectedListener: AvailableGameSelectedListener) : Int
    fun removeAvailableGame(index: Int)
    fun updateAvailableGame(availableGameInfo: AvailableGameInfo, index: Int)
}