package com.serviceMatrix.autofactory;

import com.servicematrix.msg.RequestMessage;
import java.util.concurrent.ArrayBlockingQueue;

public class AirPurifierMessageQueue {
  public static ArrayBlockingQueue<RequestMessage> AirPurifierMessageQueue = new ArrayBlockingQueue<>(100);

  public static ArrayBlockingQueue<RequestMessage> getAirPurifierMessageQueue() {
    return AirPurifierMessageQueue;
  }

  public static boolean pushAirPurifier(RequestMessage requestMessage) {
    return AirPurifierMessageQueue.offer(requestMessage);
  }

  public static RequestMessage getRequestMessage() {
    return AirPurifierMessageQueue.poll();
  }
}
