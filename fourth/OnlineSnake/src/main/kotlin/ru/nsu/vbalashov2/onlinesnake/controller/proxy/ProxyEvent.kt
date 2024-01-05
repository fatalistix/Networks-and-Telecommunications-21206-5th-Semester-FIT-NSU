package ru.nsu.vbalashov2.onlinesnake.controller.proxy

data class ProxyEvent(
    val type: ProxyEventType,
    val attachment: Any,
)
