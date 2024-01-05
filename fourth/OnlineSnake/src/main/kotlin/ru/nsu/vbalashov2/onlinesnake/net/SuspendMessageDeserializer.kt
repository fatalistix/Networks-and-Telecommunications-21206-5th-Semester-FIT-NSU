package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

interface SuspendMessageDeserializer {
    suspend fun deserialize(bytes: ByteArray, sourceHost: SourceHost): RawMessage
    suspend fun deserialize(serializedMessage: SerializedMessage): RawMessage
}