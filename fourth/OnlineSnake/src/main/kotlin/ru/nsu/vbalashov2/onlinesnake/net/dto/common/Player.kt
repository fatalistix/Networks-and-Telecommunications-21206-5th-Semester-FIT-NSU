package ru.nsu.vbalashov2.onlinesnake.net.dto.common

data class Player(
    val sourceHost: SourceHost,
    val name: String,
    val id: Int,
    val nodeRole: NodeRole,
    val score: Int,
    val hasSourceHost: Boolean,
)
