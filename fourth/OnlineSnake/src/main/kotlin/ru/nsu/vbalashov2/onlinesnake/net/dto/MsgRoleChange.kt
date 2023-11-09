package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.GameMessageInfo
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.NodeRole
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost

data class MsgRoleChange(
    val sourceHost: SourceHost,
    val gameMessageInfo: GameMessageInfo,
    val senderRole: NodeRole,
    val receiverRole: NodeRole,
    val hasSenderRole: Boolean,
    val hasReceiverRole: Boolean,
)
