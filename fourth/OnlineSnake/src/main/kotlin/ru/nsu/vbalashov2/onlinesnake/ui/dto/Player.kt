package ru.nsu.vbalashov2.onlinesnake.ui.dto

data class Player(
    val name: String,
    val score: Int,
    val isMe: Boolean,
    val isMaster: Boolean,
)
