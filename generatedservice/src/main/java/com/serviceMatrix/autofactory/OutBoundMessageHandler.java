package com.serviceMatrix.autofactory;

import com.servicematrix.client.ClientMessageSender;
import com.servicematrix.msg.RequestMessage;
import java.io.UnsupportedEncodingException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OutBoundMessageHandler implements Runnable {
  private volatile boolean stopped = false;

  private MessageController messageController;

  private ClientMessageSender clientMessageSender;

  public OutBoundMessageHandler(ClientMessageSender clientMessageSender,
      MessageController messageController) {
    this.clientMessageSender = clientMessageSender;
    this.messageController = messageController;
  }

  @Override
  public void run() {
    while(!stopped) {
      RequestMessage requestMessage = AirPurifierMessageQueue.getRequestMessage();
      if(requestMessage!=null) {
        try {
          Class clazz = messageController.getClass();
          Method method = clazz.getDeclaredMethod(InBoundMessageMap.getAPI(requestMessage.getRequestBody().getBody()));
          method.invoke(messageController);
          clientMessageSender.sendMessage(requestMessage.getRequestBody());
        }
        catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException | UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
    }
    try {
      Thread.sleep(1000);
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
}
