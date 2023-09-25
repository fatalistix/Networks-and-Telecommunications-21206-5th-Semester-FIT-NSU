package ru.nsu.vbalashov2.tcpfileserver;

import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class TCPFileServer {
  private Selector selector;
  private ServerSocketChannel serverSocketChannel;
  private final ByteBuffer byteBuffer;
  private final byte[] byteBufferTemp;

  public TCPFileServer(int bufferSize) {
    byteBuffer = ByteBuffer.allocate(bufferSize);
    byteBufferTemp = new byte[bufferSize];
  }

  public void Listen(int port) {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(port));
    selector = Selector.open();
    serverSocketChannel.configureBlocking(true);

    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      int numKeys = selector.select();
      if (numKeys == 0) {
        continue;
      }

      Set<SelectionKey> keys = selector.selectedKeys();
      for (SelectionKey key : keys) {
        if (key.isAcceptable()) {
          ServerSocketChannel acceptableChannel =
              (ServerSocketChannel)key.channel();
          SocketChannel socketChannel = acceptableChannel.accept();
          long n = 0;
          byteBuffer.clear();
          while (n < Shorts.BYTES + Longs.BYTES) {
            n += socketChannel.read(byteBuffer);
          }

          byteBuffer.flip();

          byteBuffer.get(byteBufferTemp, 0, Shorts.BYTES);
          short packageLength = Shorts.fromByteArray(byteBufferTemp);

          byteBuffer.get(byteBufferTemp, 0, Longs.BYTES);
          long fileLength = Longs.fromByteArray(byteBufferTemp);

          byteBuffer.position(byteBuffer.limit()).limit(byteBuffer.capacity());

          while (n < packageLength) {
            n += socketChannel.read(byteBuffer);
          }

          byteBuffer.get(byteBufferTemp);
          ChannelAttachment att = new ChannelAttachment(fileLength);
          att.createFile(new String(byteBufferTemp, 0,
                                    packageLength - Shorts.BYTES - Longs.BYTES,
                                    StandardCharsets.UTF_8));
          byteBuffer.clear();
          byteBuffer.put((byte)1);
          byteBuffer.clear();
          socketChannel.write(byteBuffer);

          socketChannel.register(selector, SelectionKey.OP_READ, att);
        } else if (key.isReadable()) {
          SocketChannel channel = (SocketChannel)key.channel();
          byteBuffer.clear();

          int n = channel.read(byteBuffer);
          byteBuffer.get(byteBufferTemp);
          ChannelAttachment att = (ChannelAttachment)key.attachment();
          att.write(byteBufferTemp);
          if (att.fileFullyWrote()) {
            selector.keys().remove(key);
            channel.close();
          }
        }
      }
    }
  }
}
