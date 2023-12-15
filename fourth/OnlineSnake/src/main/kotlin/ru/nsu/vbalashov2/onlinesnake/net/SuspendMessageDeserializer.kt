package ru.nsu.vbalashov2.onlinesnake.net

interface SuspendMessageSource {
    suspend fun readSuspend(): RawMessage
}