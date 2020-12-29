package com.serviceMatrix.autofactory;

import java.lang.String;
import java.util.HashMap;

public class InBoundMessageMap {
  public static HashMap<String, String> inBoundMessageMap = new HashMap<>();

  public static void init() {
    inBoundMessageMap.put("turn_on_airPurifier","turnOnAirPurifier");
  }

  public static String getAPI(String inBoundMessage) {
    return inBoundMessageMap.get(inBoundMessage);
  }
}
