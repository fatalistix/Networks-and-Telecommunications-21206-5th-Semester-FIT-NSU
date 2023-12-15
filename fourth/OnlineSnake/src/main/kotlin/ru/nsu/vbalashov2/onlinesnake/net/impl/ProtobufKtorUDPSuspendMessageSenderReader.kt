package ru.nsu.vbalashov2.onlinesnake.net.impl

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSerializer
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageDeserializer
import ru.nsu.vbalashov2.onlinesnake.net.dto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.proto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Direction as NetDirection
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.NodeRole as NetNodeRole
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.PlayerType as NetPlayerType
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.Direction as ProtobufDirection
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.NodeRole as ProtobufNodeRole
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.PlayerType as ProtobufPlayerType
import ru.nsu.vbalashov2.onlinesnake.net.dto.SnakeState as NetSnakeState
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.GameState.Snake.SnakeState as ProtobufSnakeState

class ProtobufKtorUDPSuspendMessageDeserializerSerializer private constructor(addressSpecified: Boolean, ip: String, port: Int) : SuspendMessageDeserializer, SuspendMessageSerializer {
    private val selectorManager = SelectorManager(Dispatchers.IO)

    private val socket: DatagramReadWriteChannel = if (addressSpecified) {
        aSocket(selectorManager).udp().connect(InetSocketAddress(ip, port))
    } else {
        aSocket(selectorManager).udp().bind()
    }

    constructor() : this(false, "", 0)
    constructor(ip: String, port: Int) : this(true, ip, port)

    override suspend fun readSuspend(): RawMessage {
        val datagram = socket.receive()
        val sourceHost = SourceHost(
            ip = datagram.address.toJavaAddress().address,
            port = datagram.address.toJavaAddress().port
        )
        return ProtobufRawMessage(OnlineSnakeProto.GameMessage.parseFrom(datagram.packet.readBytes())!!, sourceHost)
    }

    override suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement) {

    }

    override suspend fun writePing(msgPing: MsgPing) {

    }

    override suspend fun writeSteer(msgSteer: MsgSteer) {

    }

    override suspend fun writeJoin(msgJoin: MsgJoin) {

    }

    override suspend fun writeRoleChange(msgRoleChange: MsgRoleChange) {

    }

    override suspend fun writeError(msgError: MsgError) {

    }

    override suspend fun writeAck(msgAck: MsgAck) {

    }

    override suspend fun writeState(msgState: MsgState) {

    }







    private suspend fun packAndSend(bytes: ByteArray, sourceHost: SourceHost) {
        val datagram = Datagram(
            ByteReadPacket(
                bytes,
            ),
            InetSocketAddress(
                sourceHost.ip,
                sourceHost.port,
            ),
        )
        socket.send(datagram)
    }
}