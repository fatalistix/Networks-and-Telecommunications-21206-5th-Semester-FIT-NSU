package ru.nsu.vbalashov2.onlinesnake.ui

typealias WidthValidationRule = IntValidationRule
typealias HeightValidationRule = IntValidationRule
typealias FoodStaticValidationRule = IntValidationRule
typealias StateDelayMsValidationRule = IntValidationRule

interface GameUI {
//    fun addAvailableGame(info: AvailableGame)
//    fun removeAvailableGame(gameName: String)
    fun updateField(field: IntArray, width: Int, height: Int)
    fun addNewGameListener(listener: NewGameListener) : Int
    fun addWidthValidationRule(validationRule: WidthValidationRule) : Int
    fun addHeightValidationRule(validationRule: HeightValidationRule) : Int
    fun addFoodStaticValidationRule(validationRule: FoodStaticValidationRule) : Int
    fun addStateDelayMsValidationRule(validationRule: StateDelayMsValidationRule) : Int
}