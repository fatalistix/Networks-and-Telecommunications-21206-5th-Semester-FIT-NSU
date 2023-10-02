package ru.nsu.vbalashov2.tcpfileserver;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AttachmentsManager {
  private final Set<ChannelAttachment> attachments =
      Collections.newSetFromMap(new ConcurrentHashMap<>());

  public ChannelAttachment getNewAttachment(SocketAddress socketAddress) {
    ChannelAttachment attachment = new ChannelAttachment(socketAddress);
    attachments.add(attachment);
    return attachment;
  }

  public List<AttachmentInfo> getInfo() {
    ArrayList<AttachmentInfo> list = new ArrayList<>(attachments.size());
      for (ChannelAttachment attachment : attachments) {
          list.add(attachment.get());
      }
    return list;
  }
}
