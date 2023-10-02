package ru.nsu.vbalashov2.tcpfileclient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Input server addres: ");
    String serverAddr = scanner.next();
    scanner.nextLine();
    System.out.print("Input server port: ");
    int port = scanner.nextInt();
    scanner.nextLine();
    Client client = new Client();
    try {
      client.connect(serverAddr, port);
    } catch (IOException e) {
      System.out.println("Error connecting: " + e.getMessage());
      try {
        client.close();
      } catch (IOException ignored) {
      }
      return;
    }
    System.out.print("Input path to file: ");
    String pathToFile = scanner.next();
    scanner.nextLine();
    Path path = Path.of(pathToFile);
    if (!Files.exists(path)) {
      System.out.println("File does not exists");
      try {
        client.close();
      } catch (IOException ignored) {
      }
      return;
    }
    if (Files.isDirectory(path)) {
      System.out.println("Expected path to file, got path to directory");
      try {
        client.close();
      } catch (IOException ignored) {
      }
      return;
    }

    try {
      client.writeFile(path);
    } catch (IOException e) {
      System.out.println("Got error: " + e.getMessage());
    }
  }
}
