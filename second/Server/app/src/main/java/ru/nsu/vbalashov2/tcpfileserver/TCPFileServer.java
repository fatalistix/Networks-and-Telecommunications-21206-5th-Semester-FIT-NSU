package ru.nsu.vbalashov2.tcpfileserver;

import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import java.io.IOException;
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
  private final AttachmentsManager manager;

  public TCPFileServer(AttachmentsManager manager, int bufferSize) {
    byteBuffer = ByteBuffer.allocate(bufferSize);
    byteBufferTemp = new byte[bufferSize];
    this.manager = manager;
  }

  public void Listen(int port) throws IOException {
    try {
      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.bind(new InetSocketAddress(port));
      selector = Selector.open();
      serverSocketChannel.configureBlocking(false);

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
            SocketChannel socketChannel;
            socketChannel = acceptableChannel.accept();
            long n = 0;
            int temp = 0;
            byteBuffer.clear();
            try {
              while (n < Shorts.BYTES + Longs.BYTES) {
                temp = socketChannel.read(byteBuffer);
                if (temp == 0) {
                  closeChannel(key);
                  continue;
                }
                if (temp == -1) {
                  closeChannel(key);
                  continue;
                }
                n += temp;
              }
              byteBuffer.flip();

              byteBuffer.get(byteBufferTemp, 0, Shorts.BYTES);
              short packageLength = Shorts.fromByteArray(byteBufferTemp);

              byteBuffer.get(byteBufferTemp, 0, Longs.BYTES);
              long fileLength = Longs.fromByteArray(byteBufferTemp);

              byteBuffer.position(byteBuffer.limit())
                  .limit(byteBuffer.capacity());

              while (n < packageLength) {
                temp = socketChannel.read(byteBuffer);
                if (temp == 0) {
                  closeChannel(key);
                  continue;
                }
                if (temp == -1) {
                  closeChannel(key);
                  continue;
                }
                n += temp;
              }
              byteBuffer.flip();
              byteBuffer.position(Shorts.BYTES + Longs.BYTES);
              byteBuffer.get(byteBufferTemp, 0, byteBuffer.remaining());
              ChannelAttachment att =
                  manager.getNewAttachment(socketChannel.getRemoteAddress());
              att.createFile(
                  new String(byteBufferTemp, 0,
                             packageLength - Shorts.BYTES - Longs.BYTES,
                             StandardCharsets.UTF_8),
                  fileLength);
              byteBuffer.clear();
              byteBuffer.put((byte)1);
              byteBuffer.flip();
              socketChannel.write(byteBuffer);
              socketChannel.configureBlocking(false);
              socketChannel.register(selector, SelectionKey.OP_READ, att);
            } catch (IOException e) {
              closeChannel(key);
            }

          } else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel)key.channel();
            byteBuffer.clear();
            ChannelAttachment att = (ChannelAttachment)key.attachment();
            try {
              int temp = channel.read(byteBuffer);
              if (temp == -1) {
                closeChannel(key);
              }
              byteBuffer.flip();
              int length = byteBuffer.remaining();
              byteBuffer.get(byteBufferTemp, 0, length);
              att.write(byteBufferTemp, length);
              if (att.fileFullyWrote()) {
                closeChannel(key);
              }
            } catch (IOException e) {
              closeChannel(key);
            }
          }
        }
        selector.selectedKeys().clear();
      }
    } catch (IOException e) {
      close();
      throw e;
    }
  }

  public void close() {
    for (SelectionKey key : selector.keys()) {
      closeChannel(key);
    }
    try {
      selector.close();
    } catch (IOException ignored) {
    }
  }

  private void closeChannel(SelectionKey key) {
    try {
      key.channel().close();
      if (key.attachment() != null) {
        ((ChannelAttachment)key.attachment()).close();
      }
    } catch (IOException ignored) {
    }
  }
}
