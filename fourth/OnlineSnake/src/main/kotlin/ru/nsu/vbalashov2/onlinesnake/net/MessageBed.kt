package ru.nsu.vbalashov2.onlinesnake.net

import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement

interface MessageBed {
    fun writeAnnouncement(msgAnnouncement: MsgAnnouncement)
}