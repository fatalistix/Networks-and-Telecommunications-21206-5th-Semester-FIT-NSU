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
import ru.nsu.vbalashov2.onlinesnake.net.dto.MsgPing
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.proto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.NodeRole as NetNodeRole
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.NodeRole as ProtobufNodeRole

class ProtobufUDPSuspendMessageSourceEnd private constructor(addressSpecified: Boolean, ip: String, port: Int) : SuspendMessageSource, SuspendMessageEnd {
    private val selectorManager = SelectorManager(Dispatchers.IO)

    private val socket = if (addressSpecified) {
        aSocket(selectorManager).udp().bind(InetSocketAddress(ip, port))
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
        println("address = " + datagram.address.toJavaAddress().address)
        println("hostname = " + datagram.address.toJavaAddress().hostname)
        return ProtobufRawMessage(OnlineSnakeProto.GameMessage.parseFrom(datagram.packet.readBytes())!!, sourceHost)
    }

    override suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement) {
        val announcementMsg = GameMessageKt.announcementMsg { }
        announcementMsg.gamesList += msgAnnouncement.gameAnnouncementList.map { announcement ->
            gameAnnouncement {
                players = gamePlayers { }
                players.playersList += announcement.playerList.map { p ->
                    gamePlayer {
                        this.name = p.name
                        this.id = p.id
                        this.role = netNodeRoleToProtobufNodeRole(p.nodeRole)
                        this.score = p.score
                        if (p.hasSourceHost) {
                            this.ipAddress = p.sourceHost.ip
                            this.port = p.sourceHost.port
                        }
                    }
                }
                config = gameConfig {
                    height = announcement.gameConfig.height
                    width = announcement.gameConfig.width
                    foodStatic = announcement.gameConfig.foodStatic
                    stateDelayMs = announcement.gameConfig.stateDelayMs
                }
                canJoin = announcement.canJoin
                gameName = announcement.gameName
            }
        }
        packAndSend(announcementMsg.toByteArray(), msgAnnouncement.sourceHost)
    }

    override suspend fun writePing(msgPing: MsgPing) {
        TODO("NOT IMPLEMENTED YET")
    }

    private fun netNodeRoleToProtobufNodeRole(netNodeRole: NetNodeRole): ProtobufNodeRole {
        return when (netNodeRole) {
            NetNodeRole.VIEWER -> ProtobufNodeRole.VIEWER
            NetNodeRole.DEPUTY -> ProtobufNodeRole.DEPUTY
            NetNodeRole.MASTER -> ProtobufNodeRole.MASTER
            NetNodeRole.NORMAL -> ProtobufNodeRole.NORMAL
        }
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