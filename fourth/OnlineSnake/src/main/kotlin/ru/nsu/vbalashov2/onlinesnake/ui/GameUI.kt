package ru.nsu.vbalashov2.onlinesnake.ui

interface GameUI {
//    fun addAvailableGame(info: AvailableGame)
//    fun removeAvailableGame(gameName: String)
    fun updateField(field: IntArray, width: Int, height: Int)
    fun addStartGameListener(listener: (width: Int, height: Int, foodStatic: Int, stateDelayMs: Int) -> Unit) : Int
    fun addWidthValidationRule(validationRule: (width: Int) -> Boolean) : Int
    fun addHeightValidationRule(validationRule: (height: Int) -> Boolean) : Int
    fun addFoodStaticValidationRule(validationRule: (foodStatic: Int) -> Boolean) : Int
    fun addStateDelayMsValidationRule(validationRule: (stateDelayMs: Int) -> Boolean) : Int
}