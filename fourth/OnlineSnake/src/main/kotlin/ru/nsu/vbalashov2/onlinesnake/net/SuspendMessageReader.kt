package ru.nsu.vbalashov2.onlinesnake.net

interface SuspendMessageReader {
    suspend fun read(): SerializedMessage
}