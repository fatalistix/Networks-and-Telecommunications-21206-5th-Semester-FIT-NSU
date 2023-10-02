package ru.nsu.vbalashov2.tcpfileclient;

import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Client {
  private SocketChannel socketChannel;

  public void connect(String address, int port) throws IOException {
    socketChannel = SocketChannel.open(new InetSocketAddress(address, port));
  }

  public void writeFile(Path pathToFile) throws IOException {
    try (InputStream in =
             Files.newInputStream(pathToFile, StandardOpenOption.READ)) {
      ByteBuffer bb = ByteBuffer.allocate(8192);
      byte[] tempBuffer = new byte[8192];

      bb.put(Shorts.toByteArray(
          (short)(Shorts.BYTES + Long.BYTES +
                  pathToFile.getFileName().toString().length())));
      bb.put(Longs.toByteArray(Files.size(pathToFile)));
      bb.put(
          pathToFile.getFileName().toString().getBytes(StandardCharsets.UTF_8));
      bb.limit(bb.position()).flip();

      socketChannel.write(bb);
      bb.clear();
      socketChannel.read(bb);

      int length;

      while (true) {
        length = in.read(tempBuffer);
        if (length == 0 || length == -1) {
          try {
            in.close();
          } catch (IOException ignored) {
          }
          try {
            socketChannel.close();
          } catch (IOException ignored) {
          }
          break;
        }
        bb.clear();
        bb.put(tempBuffer, 0, length);
        bb.limit(bb.position()).flip();
        socketChannel.write(bb);
      }
    } catch (IOException e) {
      try {
        socketChannel.close();
      } catch (IOException ignored) {
      }
      throw e;
    }
  }

  public void close() throws IOException { socketChannel.close(); }
}
