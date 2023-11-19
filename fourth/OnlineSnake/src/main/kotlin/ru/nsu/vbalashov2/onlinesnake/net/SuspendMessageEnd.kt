package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.*

interface SuspendMessageEnd {
    suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement)
    suspend fun writePing(msgPing: MsgPing)
    suspend fun writeSteer(msgSteer: MsgSteer)
    suspend fun writeJoin(msgJoin: MsgJoin)
    suspend fun writeRoleChange(msgRoleChange: MsgRoleChange)
    suspend fun writeError(msgError: MsgError)
}