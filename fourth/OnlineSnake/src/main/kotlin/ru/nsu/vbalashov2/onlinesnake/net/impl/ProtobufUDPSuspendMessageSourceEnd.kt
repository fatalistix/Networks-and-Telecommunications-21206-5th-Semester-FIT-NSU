package ru.nsu.vbalashov2.onlinesnake.net.impl

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import ru.nsu.vbalashov2.onlinesnake.net.RawMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageEnd
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSource
import ru.nsu.vbalashov2.onlinesnake.net.dto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import ru.nsu.vbalashov2.onlinesnake.proto.*
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.Direction as NetDirection
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.NodeRole as NetNodeRole
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.PlayerType as NetPlayerType
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.Direction as ProtobufDirection
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.NodeRole as ProtobufNodeRole
import ru.nsu.vbalashov2.onlinesnake.proto.OnlineSnakeProto.PlayerType as ProtobufPlayerType

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
        return ProtobufRawMessage(OnlineSnakeProto.GameMessage.parseFrom(datagram.packet.readBytes())!!, sourceHost)
    }

    override suspend fun writeAnnouncement(msgAnnouncement: MsgAnnouncement) {
        val announcementMsg = GameMessageKt.announcementMsg {
            this.games.addAll(msgAnnouncement.gameAnnouncementList.map { announcement ->
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
            })
        }
        val gameMessage = gameMessage {
            this.announcement = announcementMsg
            this.msgSeq = msgAnnouncement.gameMessageInfo.msgSeq
            if (msgAnnouncement.gameMessageInfo.hasSenderID) {
                this.senderId = msgAnnouncement.gameMessageInfo.senderID
            }
            if (msgAnnouncement.gameMessageInfo.hasReceiverId) {
                this.receiverId = msgAnnouncement.gameMessageInfo.receiverID
            }
        }
        packAndSend(gameMessage.toByteArray(), msgAnnouncement.sourceHost)
    }

    override suspend fun writePing(msgPing: MsgPing) {
        TODO("NOT IMPLEMENTED YET")
    }

    override suspend fun writeSteer(msgSteer: MsgSteer) {
        val steerMsg = GameMessageKt.steerMsg {
            this.direction = netDirectionToProtobufDirection(msgSteer.newDirection)
        }
        val gameMessage = gameMessage {
            this.steer = steerMsg
            this.msgSeq = msgSteer.gameMessageInfo.msgSeq
            if (msgSteer.gameMessageInfo.hasSenderID) {
                this.senderId = msgSteer.gameMessageInfo.senderID
            }
            if (msgSteer.gameMessageInfo.hasReceiverId) {
                this.receiverId = msgSteer.gameMessageInfo.receiverID
            }
        }
        packAndSend(gameMessage.toByteArray(), msgSteer.sourceHost)
    }

    override suspend fun writeJoin(msgJoin: MsgJoin) {
        val joinMsg = GameMessageKt.joinMsg {
            this.gameName = msgJoin.gameName
            this.requestedRole = netNodeRoleToProtobufNodeRole(msgJoin.requestedRole)
            this.playerName = msgJoin.playerName
            this.playerType = netPlayerTypeToProtobufPlayerType(msgJoin.playerType)
        }
        val gameMessage = gameMessage {
            this.join = joinMsg
            this.msgSeq = msgJoin.gameMessageInfo.msgSeq
            if (msgJoin.gameMessageInfo.hasSenderID) {
                this.senderId = msgJoin.gameMessageInfo.senderID
            }
            if (msgJoin.gameMessageInfo.hasReceiverId) {
                this.receiverId = msgJoin.gameMessageInfo.receiverID
            }
        }
        packAndSend(gameMessage.toByteArray(), msgJoin.sourceHost)
    }

    @Throws(IllegalArgumentException::class)
    override suspend fun writeRoleChange(msgRoleChange: MsgRoleChange) {
        if (!msgRoleChange.gameMessageInfo.hasSenderID || !msgRoleChange.gameMessageInfo.hasReceiverId) {
            throw IllegalArgumentException("Expected sender ID and receiver ID")
        }
        val roleChangeMsg = GameMessageKt.roleChangeMsg {
            if (msgRoleChange.hasReceiverRole) {
                this.receiverRole = netNodeRoleToProtobufNodeRole(msgRoleChange.receiverRole)
            }
            if (msgRoleChange.hasSenderRole) {
                this.senderRole = netNodeRoleToProtobufNodeRole(msgRoleChange.senderRole)
            }
        }
        val gameMessage = gameMessage {
            this.roleChange = roleChangeMsg
            this.msgSeq = msgRoleChange.gameMessageInfo.msgSeq
            this.senderId = msgRoleChange.gameMessageInfo.senderID
            this.receiverId = msgRoleChange.gameMessageInfo.receiverID
        }
        packAndSend(gameMessage.toByteArray(), msgRoleChange.sourceHost)
    }

    override suspend fun writeError(msgError: MsgError) {
        val errorMsg = GameMessageKt.errorMsg {
            this.errorMessage = msgError.errorMessage
        }
        val gameMessage = gameMessage {
            this.error = errorMsg
            this.msgSeq = msgError.gameMessageInfo.msgSeq
            if (msgError.gameMessageInfo.hasReceiverId) {
                this.receiverId = msgError.gameMessageInfo.receiverID
            }
            if (msgError.gameMessageInfo.hasSenderID) {
                this.senderId = msgError.gameMessageInfo.senderID
            }
        }
        packAndSend(gameMessage.toByteArray(), msgError.sourceHost)
    }

    private fun netNodeRoleToProtobufNodeRole(netNodeRole: NetNodeRole): ProtobufNodeRole {
        return when (netNodeRole) {
            NetNodeRole.VIEWER -> ProtobufNodeRole.VIEWER
            NetNodeRole.DEPUTY -> ProtobufNodeRole.DEPUTY
            NetNodeRole.MASTER -> ProtobufNodeRole.MASTER
            NetNodeRole.NORMAL -> ProtobufNodeRole.NORMAL
        }
    }

    private fun netDirectionToProtobufDirection(netDirection: NetDirection): ProtobufDirection {
        return when (netDirection) {
            NetDirection.DOWN -> ProtobufDirection.DOWN
            NetDirection.LEFT -> ProtobufDirection.LEFT
            NetDirection.RIGHT -> ProtobufDirection.RIGHT
            NetDirection.UP -> ProtobufDirection.UP
        }
    }

    private fun netPlayerTypeToProtobufPlayerType(netPlayerType: NetPlayerType): ProtobufPlayerType {
        return when (netPlayerType) {
            NetPlayerType.ROBOT -> ProtobufPlayerType.ROBOT
            NetPlayerType.HUMAN -> ProtobufPlayerType.HUMAN
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