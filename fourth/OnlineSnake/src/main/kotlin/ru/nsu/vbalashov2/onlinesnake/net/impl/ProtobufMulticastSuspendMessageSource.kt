package ru.nsu.vbalashov2.onlinesnake.net.impl

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSource
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto

class ProtobufMulticastSuspendMessageSource : SuspendMessageSource {
    private val mcastIP = "239.192.0.4"
    private val port = 9192
    private val selectorManager = SelectorManager(Dispatchers.IO)
    private val socket = aSocket(selectorManager).udp().bind(InetSocketAddress(mcastIP, port))

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
}