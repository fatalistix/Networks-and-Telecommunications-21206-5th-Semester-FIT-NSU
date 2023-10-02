package ru.nsu.vbalashov2.tcpfileserver;

import java.net.SocketAddress;
import java.time.Instant;

public record AttachmentInfo(String fileName, long fileLength,
                             SocketAddress remoteAddr, long alreadyWrote,
                             long wroteSinceLastGet, Instant creationTime, boolean completed, Instant completionTime) {}
