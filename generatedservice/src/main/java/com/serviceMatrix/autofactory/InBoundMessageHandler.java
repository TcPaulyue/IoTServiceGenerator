package com.serviceMatrix.autofactory;

import com.servicematrix.client.netty.ServerMessageHandler;
import com.servicematrix.msg.RequestMessage;
import java.lang.Override;
import java.lang.String;

public class InBoundMessageHandler extends ServerMessageHandler {
  @Override
  public void responseMessage(RequestMessage requestMessage) {
    AirPurifierMessageQueue.pushAirPurifier(requestMessage);
  }

  @Override
  public void ackMessage(String s) {
    System.out.println(s);
  }
}
