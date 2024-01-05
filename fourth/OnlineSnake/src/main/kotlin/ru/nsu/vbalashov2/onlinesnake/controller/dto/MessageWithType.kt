package ru.nsu.vbalashov2.onlinesnake.controller.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.MessageType
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

data class MessageWithType(
    val messageType: MessageType,
    val sourceHost: SourceHost,
    val message: Any,
)
