package ru.nsu.vbalashov2.tcpfileserver;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SetupCli implements Setup {
  private final Settings settings = new Settings();

  @Override
  public void setup() {
    System.out.println("<==========================================>");
    System.out.println("Welcome to TCP File Server!");
    System.out.println("Impl by ~@v.balashov2@g.nsu.ru");
    System.out.println("<==========================================>");
    System.out.print("Type server's listening port: ");
    scanForPort();
  }

  @Override
  public Settings getSettings() {
    return settings;
  }

  @Override
  public void updatePort() {
    System.out.print("Please, enter new port: ");
    scanForPort();
  }

  private void scanForPort() {
    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        try {
          settings.listeningPort = scanner.nextInt();
          if (settings.listeningPort < 0 ||
              Short.MAX_VALUE * 2 + 1 < settings.listeningPort) {
            throw new IllegalArgumentException();
          }
          break;
        } catch (InputMismatchException | IllegalArgumentException e) {
          System.out.println(
              "Error: expected 0 <= number <= 65535 in decimal format");
          scanner.nextLine();
          System.out.print("Type server's listening port: ");
        }
      }
    }

    System.out.println("Port saved");
  }
}
