package com.serviceMatrix.autofactory;

import com.servicematrix.client.ClientMessageSender;
import io.vertx.core.Vertx;
import java.lang.String;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    try {
      Vertx vertx = Vertx.vertx();
      vertx.deployVerticle(new AirPurifierController(new AirPurifier()));
      InBoundMessageMap.init();
      ExecutorService executorService = Executors.newFixedThreadPool(1);
      ClientMessageSender clientMessageSender = new ClientMessageSender("localhost",8082, new InBoundMessageHandler());
      executorService.execute(new OutBoundMessageHandler(clientMessageSender,new MessageController()));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
