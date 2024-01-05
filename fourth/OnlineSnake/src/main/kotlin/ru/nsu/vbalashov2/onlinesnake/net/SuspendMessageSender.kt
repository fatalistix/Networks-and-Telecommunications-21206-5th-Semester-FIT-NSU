package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

interface SuspendMessageSender {
    suspend fun send(bytes: ByteArray, targetSourceHost: SourceHost)
    suspend fun send(serializedMessage: SerializedMessage) {
        send(serializedMessage.bytes, serializedMessage.sourceHost)
    }
}