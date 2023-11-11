package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement

interface SuspendMessageEnd {
    suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement)
}