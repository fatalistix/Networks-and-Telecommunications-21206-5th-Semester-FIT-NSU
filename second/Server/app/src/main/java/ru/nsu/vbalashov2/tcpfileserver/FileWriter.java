package ru.nsu.vbalashov2.tcpfileserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileWriter {
  private Path finalFilePath;
  // private final long fileLength;
  // private long alreadyWrote = 0;
  private OutputStream fileOutputStream;

  // public FileWriter(long fileLength) { this.fileLength = fileLength; }

  public void createFile(String fileName) throws IOException {
    Path uploadsDirPath = Path.of(".", "uploads");
    if (!Files.exists(uploadsDirPath)) {
      Files.createDirectory(uploadsDirPath);
    }
    Path filePath = uploadsDirPath.resolve(fileName);
    if (Files.exists(filePath)) {
      String[] splitted = fileName.split("\\.");
      int index = 0;
      Path filePathWithNum;
      StringBuilder fileNameWithoutExt = new StringBuilder(splitted[0]);
      if (splitted.length != 1) {
        for (int i = 1; i < splitted.length - 1; ++i) {
          fileNameWithoutExt.append(".").append(splitted[i]);
        }
      }

      do {
        if (splitted.length == 1) {
          filePathWithNum =
              uploadsDirPath.resolve(fileNameWithoutExt + "(" + index + ")");
        } else {
          filePathWithNum =
              uploadsDirPath.resolve(fileNameWithoutExt + "(" + index + ")." +
                                     splitted[splitted.length - 1]);
        }
        ++index;
      } while (Files.exists(filePathWithNum));
      // Files.createFile(filePathWithNum);
      finalFilePath = filePathWithNum;
      fileOutputStream =
          Files.newOutputStream(finalFilePath, StandardOpenOption.CREATE_NEW,
                                StandardOpenOption.APPEND);
    } else {
      // Files.createFile(filePath);
      finalFilePath = filePath;
      fileOutputStream =
          Files.newOutputStream(finalFilePath, StandardOpenOption.CREATE_NEW,
                                StandardOpenOption.APPEND);
    }
  }

  public void write(byte[] bytes, int length) throws IOException {
    // Files.write(finalFilePath, bytes, StandardOpenOption.APPEND);
    fileOutputStream.write(bytes, 0, length);
    // alreadyWrote += length;
  }

  // public boolean fileFullyWrote() { return alreadyWrote == fileLength; }

  public void close() throws IOException { fileOutputStream.close(); }

  public String getFileName() { return finalFilePath.getFileName().toString(); }
}
