package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgPing

interface SuspendMessageEnd {
    suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement)
    suspend fun writePing(msgPing: MsgPing)
}