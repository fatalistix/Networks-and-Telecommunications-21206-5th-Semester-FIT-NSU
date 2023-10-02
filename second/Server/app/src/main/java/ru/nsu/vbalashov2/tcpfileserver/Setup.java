package ru.nsu.vbalashov2.tcpfileserver;

public interface Setup {
  void setup();

  Settings getSettings();

  void updatePort();
}
