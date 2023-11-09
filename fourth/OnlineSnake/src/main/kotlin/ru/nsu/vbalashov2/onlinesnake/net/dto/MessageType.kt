package ru.nsu.vbalashov2.onlinesnake.net.dto

enum class MessageType {
    PING,
    STEER,
    ACK,
    STATE,
    ANNOUNCEMENT,
    JOIN,
    ERROR,
    ROLE_CHANGE,
    DISCOVER,
    UNKNOWN
}