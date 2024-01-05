package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

interface RawMessage {
    val type: MessageType
    val sourceHost: SourceHost
    val gameMessageInfo: GameMessageInfo
    fun getAsPing(): MsgPing
    fun getAsSteer(): MsgSteer
    fun getAsAck(): MsgAck
    fun getAsState(): MsgState
    fun getAsAnnouncement(): MsgAnnouncement
    fun getAsJoin(): MsgJoin
    fun getAsError(): MsgError
    fun getAsRoleChange(): MsgRoleChange
    fun getAsDiscover(): MsgDiscover
}
