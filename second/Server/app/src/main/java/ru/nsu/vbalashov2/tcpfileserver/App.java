package ru.nsu.vbalashov2.tcpfileserver;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class App {
  private static final int bufferSize = 1024;
  private static final int timerTimeMilis = 3 * 1000;

  public static void main(String[] args) {
    Setup setup = new SetupCli();
    setup.setup();
    Settings settings = setup.getSettings();
    AttachmentsManager manager = new AttachmentsManager();
    TCPFileServer server = new TCPFileServer(manager, bufferSize);
    Timer speedPrinter = new Timer();
    speedPrinter.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            List<AttachmentInfo> infos = manager.getInfo();
            var iter = infos.iterator();
            System.out.println("<===> Next 3 seconds <===>");
            Instant now = Instant.now();
            while (iter.hasNext()) {
              AttachmentInfo attInfo = iter.next();
              if (attInfo.completed()) {
                System.out.println(attInfo.fileName() + " from " + attInfo.remoteAddr().toString()
                 + " completed: downloaded " + (double) attInfo.alreadyWrote() / attInfo.fileLength() * 100 + "% or "
                + attInfo.alreadyWrote() + " bytes of " + attInfo.fileLength() + ", average speed = "+ (double) attInfo.alreadyWrote()
                        / ChronoUnit.MILLIS.between(attInfo.creationTime(), attInfo.completionTime()) * 1000. / 1024. + " KBytes/sec");
              } else {
                System.out.println(
                        attInfo.fileName()
                                + " from "
                                + attInfo.remoteAddr().toString()
                                + ": instant speed = "
                                + (double) attInfo.wroteSinceLastGet() / timerTimeMilis * 1000. / 1024.
                                + " KBytes/sec; average speed = "
                                + (double) attInfo.alreadyWrote()
                                / ChronoUnit.MILLIS.between(attInfo.creationTime(), now) * 1000. / 1024.
                                + " KBytes/sec; downloaded "
                                + (double) attInfo.alreadyWrote() / attInfo.fileLength() * 100
                                + "%");
              }
            }
          }
        },
        0,
        timerTimeMilis);
    try {
      server.Listen(settings.listeningPort);
    } catch (IOException e) {
      System.out.println("ERROR: SERVER CLOSED WITH MESSAGE: " + e.getMessage());
    }
  }
}
