package ru.nsu.vbalashov2.tcpfileserver;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelAttachment {
  private final SocketAddress socketAddress;
  private String fileName;
  private long fileLength;
  private final FileWriter fileWriter = new FileWriter();
  private AtomicLong alreadyWrote = new AtomicLong();
  private AtomicLong wroteSinceLastGet = new AtomicLong();
  private Instant creationTime;
  private boolean completed = false;
  private Instant completionTime;

  public ChannelAttachment(SocketAddress socketAddress) {
    this.socketAddress = socketAddress;
  }

  public void createFile(String fileName, long fileLength) throws IOException {
    fileWriter.createFile(fileName);
    this.fileLength = fileLength;
    this.fileName = fileWriter.getFileName();
    this.creationTime = Instant.now();
  }

  public void write(byte[] bytes, int length) throws IOException {
    fileWriter.write(bytes, length);
    alreadyWrote.getAndAdd(length);
    wroteSinceLastGet.getAndAdd(length);
    if (fileFullyWrote()) {
      completed = true;
      completionTime = Instant.now();
    }
  }

  public boolean fileFullyWrote() { return alreadyWrote.get() == fileLength; }

  public void close() throws IOException {
    completed = true;
    completionTime = Instant.now();
    fileWriter.close();
  }

  public AttachmentInfo get() {
    return new AttachmentInfo(fileName, fileLength, socketAddress,
                              alreadyWrote.get(),
                              wroteSinceLastGet.getAndSet(0), creationTime, completed, completionTime);
  }
}
