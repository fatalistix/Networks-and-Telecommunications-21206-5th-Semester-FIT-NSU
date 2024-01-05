package ru.nsu.vbalashov2.onlinesnake.controller.dto

import java.time.Instant

data class MessageWithTypeAndCreationTime(
    val messageWithType: MessageWithType,
    val creationInstant: Instant,
)
