package ru.nsu.vbalashov2.onlinesnake.controller.oldproxy

data class ProxyEvent(
    val type: ProxyEventType,
    val attachment: Any,
)
