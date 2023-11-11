package ru.nsu.vbalashov2.onlinesnake.ui

fun interface IntValidationRule {
    fun validate(property: Int): Boolean
}