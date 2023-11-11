package ru.nsu.vbalashov2.onlinesnake.net.impl

import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageEnd
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement

class ProtobufUDPSuspendMessageEnd : SuspendMessageEnd {
    override suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement) {
        TODO("Not yet implemented")
    }
}