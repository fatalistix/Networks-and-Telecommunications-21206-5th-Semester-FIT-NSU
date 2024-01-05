package ru.nsu.vbalashov2.onlinesnake.net.dto

import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.*

data class MsgAnnouncement(
    val sourceHost: SourceHost,
    val gameMessageInfo: GameMessageInfo,
    val gameAnnouncementList: List<GameAnnouncement>
)

