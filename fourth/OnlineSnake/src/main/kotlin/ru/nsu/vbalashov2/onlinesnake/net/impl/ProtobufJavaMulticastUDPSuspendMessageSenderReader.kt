package ru.nsu.vbalashov2.onlinesnake.net.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nsu.vbalashov2.onlinesnake.net.SerializedMessage
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageReader
import ru.nsu.vbalashov2.onlinesnake.net.SuspendMessageSender
import ru.nsu.vbalashov2.onlinesnake.net.dto.common.SourceHost
import java.io.IOException
import java.net.*

class ProtobufJavaMulticastUDPSuspendMessageSenderReader(
    ip: String,
    port: Int,
) : SuspendMessageSender, SuspendMessageReader {
    private companion object {
        const val BYTES_SIZE = 10_000
    }
    private val multicastSocket = MulticastSocket(port)
    private val addr = InetAddress.getByName(ip)
    private val group = InetSocketAddress(addr, port)

    private val niface: NetworkInterface

    init {
        val allNifaces = NetworkInterface.getNetworkInterfaces()!!
//        val iter = NetworkInterface.getNetworkInterfaces()!!.iterator()
//        while (iter.hasNext()) {
//            println("ITERATOR NIFACE: ${iter.next().name}")
//        }
        niface = allNifaces.toList().find { networkInterface ->
            !networkInterface.isLoopback && networkInterface.isUp && networkInterface.supportsMulticast()
        } ?: throw IOException("no interfaces found")
        allNifaces.toList().forEach { println("NIFACE: ${it.name}") }
//        println(allNifaces.toList().size)
        println(niface.name)
    }

    init {
        multicastSocket.joinGroup(group, niface)
    }

    private val bytes = ByteArray(BYTES_SIZE)
    private val datagramPacket = DatagramPacket(bytes, BYTES_SIZE)

    @Throws(IOException::class)
    override suspend fun read(): SerializedMessage {
        withContext(Dispatchers.IO) {
            multicastSocket.receive(datagramPacket)
        }
        return SerializedMessage(
            bytes = datagramPacket.data.copyOf(datagramPacket.length),
            sourceHost = SourceHost(
                ip = datagramPacket.address.hostAddress,
                port = datagramPacket.port
            )
        )
    }

    @Throws(IOException::class)
    override suspend fun send(bytes: ByteArray, targetSourceHost: SourceHost) {
        val sendDatagramPacket = DatagramPacket(
            bytes,
            bytes.size,
            InetSocketAddress(targetSourceHost.ip, targetSourceHost.port)
        )
        withContext(Dispatchers.IO) {
            multicastSocket.send(sendDatagramPacket)
        }
    }
}