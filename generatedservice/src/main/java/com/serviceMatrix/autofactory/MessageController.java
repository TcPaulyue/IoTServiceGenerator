package com.serviceMatrix.autofactory;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class MessageController {
  public boolean turnOnAirPurifier() {
    String url = "http://192.168.31.143:8123/api/services/fan/turn_on";
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("entity_id","fan.xiaomi_miio_device");
    StringEntity entity = null;
    try {
      entity = new StringEntity(jsonObject.toString());
      httpPost.setEntity(entity);
      httpPost.setHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1ZWZiNmY5MzNhYTE0YzcyODAyZGRlMzBiYWMyZTc3ZCIsImlhdCI6MTYwMzY4MTAyMywiZXhwIjoxOTE5MDQxMDIzfQ.QnqQtBFPnhbkr6DtKth0YbD7E3Q31HT-kwIlOevjGV4");
      httpPost.setHeader("Content-type","application/json");
      HttpResponse response = client.execute(httpPost);
      client.close();
      System.out.println(response.toString());
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    System.out.println("12334444");
    return true;
  }
}
