package ru.nsu.vbalashov2.onlinesnake.net.impl

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageEnd
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSource
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgAnnouncement
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto

class ProtobufUDPSuspendMessageSourceEnd(ip: String, port: Int) : SuspendMessageSource, SuspendMessageEnd {
    private val selectorManager = SelectorManager(Dispatchers.IO)
    private val socket = aSocket(selectorManager).udp().bind(InetSocketAddress(ip, port))

    override suspend fun readSuspend(): RawMessage {
        val datagram = socket.receive()
        val sourceHost = SourceHost(
            ip = datagram.address.toJavaAddress().address,
            port = datagram.address.toJavaAddress().port
        )
        println("address = " + datagram.address.toJavaAddress().address)
        println("hostname = " + datagram.address.toJavaAddress().hostname)
        return ProtobufRawMessage(OnlineSnakeProto.GameMessage.parseFrom(datagram.packet.readBytes())!!, sourceHost)
    }

    override suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement) {

    }
}