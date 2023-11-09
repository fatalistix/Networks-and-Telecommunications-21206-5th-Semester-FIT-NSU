package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.*

interface RawMessage {
    fun getType(): MessageType
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