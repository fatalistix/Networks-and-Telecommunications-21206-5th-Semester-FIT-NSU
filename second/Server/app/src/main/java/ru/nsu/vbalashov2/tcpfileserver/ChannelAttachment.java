package ru.nsu.vbalashov2.tcpfileserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ChannelAttachment {
  private Path finalFilePath;
  private final long fileLength;
  private long alreadyWrote = 0;

  public ChannelAttachment(long fileLength) { this.fileLength = fileLength; }

  public void createFile(String fileName) throws IOException {
    Path uploadsDirPath = Path.of(".", "uploads");
    if (!Files.exists(uploadsDirPath)) {
      Files.createDirectory(uploadsDirPath);
    }
    Path filePath = uploadsDirPath.resolve(fileName);
    if (Files.exists(filePath)) {
      String[] splitted = fileName.split(".");
      int index = 0;
      Path filePathWithNum;
      String fileNameWithoutExt = splitted[0];
      if (splitted.length != 1) {
        for (int i = 1; i < splitted.length - 1; ++i) {
          fileNameWithoutExt += splitted[i];
        }
      }

      do {
        if (splitted.length == 1) {
          filePathWithNum =
              uploadsDirPath.resolve(fileNameWithoutExt + "(" + index + ")");
        } else {
          filePathWithNum =
              uploadsDirPath.resolve(fileNameWithoutExt + "(" + index + ")" +
                                     splitted[splitted.length - 1]);
        }
      } while (!Files.exists(filePathWithNum));
      Files.createFile(filePathWithNum);
      finalFilePath = filePathWithNum;
    } else {
      Files.createFile(filePath);
      finalFilePath = filePath;
    }
  }

  public void write(byte[] bytes) throws IOException {
    Files.write(finalFilePath, bytes, StandardOpenOption.APPEND);
    alreadyWrote += bytes.length;
  }

  public boolean fileFullyWrote() { return alreadyWrote == fileLength; }
}
