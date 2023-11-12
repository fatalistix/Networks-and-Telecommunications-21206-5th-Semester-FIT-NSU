package ru.nsu.vbalashov2.onlinesnake.ui

import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint

typealias WidthValidationRule = IntValidationRule
typealias HeightValidationRule = IntValidationRule
typealias FoodStaticValidationRule = IntValidationRule
typealias StateDelayMsValidationRule = IntValidationRule

interface GameUI {
//    fun addAvailableGame(info: AvailableGame)
//    fun removeAvailableGame(gameName: String)
//    fun updateField(field: IntArray, width: Int, height: Int)
    fun updateField(snakesKeyPointsList: List<List<KeyPoint>>, foodList: List<KeyPoint>, width: Int, height: Int)
    fun addNewGameListener(listener: NewGameListener) : Int
    fun addExitListener(listener: ExitListener) : Int
    fun addWidthValidationRule(validationRule: WidthValidationRule) : Int
    fun addHeightValidationRule(validationRule: HeightValidationRule) : Int
    fun addFoodStaticValidationRule(validationRule: FoodStaticValidationRule) : Int
    fun addStateDelayMsValidationRule(validationRule: StateDelayMsValidationRule) : Int
}