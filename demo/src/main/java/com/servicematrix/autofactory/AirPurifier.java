package com.servicematrix.autofactory;

import com.servicematrix.autofactory.annotations.APIConfigration;
import com.servicematrix.autofactory.annotations.Device;
import com.servicematrix.autofactory.annotations.DeviceElement;
import com.servicematrix.autofactory.annotations.InBoundMessage;

@Device
@APIConfigration(url = "http://192.168.31.143:8123/api/services/fan/turn_on"
        ,Authorization = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1ZWZiNmY" +
                "5MzNhYTE0YzcyODAyZGRlMzBiYWMyZTc3ZCIsImlhdCI6MTYwMzY4MTAyMywiZXhw" +
                "IjoxOTE5MDQxMDIzfQ.QnqQtBFPnhbkr6DtKth0YbD7E3Q31HT-kwIlOevjGV4"
        ,entity_id = "fan.xiaomi_miio_device")
public class AirPurifier {
    @DeviceElement(api = "api/speed")
    public String speed;

    @DeviceElement
    public String name;

    @InBoundMessage(message = "turn_on_airPurifier")
    public Boolean turnOnAirPurifier(){
        System.out.println("12334444");
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://192.168.31.143:8123/api/services/fan/turn_on";
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1ZWZiNmY" +
//                "5MzNhYTE0YzcyODAyZGRlMzBiYWMyZTc3ZCIsImlhdCI6MTYwMzY4MTAyMywiZXhw" +
//                "IjoxOTE5MDQxMDIzfQ.QnqQtBFPnhbkr6DtKth0YbD7E3Q31HT-kwIlOevjGV4");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("entity_id","fan.xiaomi_miio_device");
//        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(),headers);
//        String response = restTemplate.postForObject(url,request,String.class);
//        System.out.println(response);
        return true;
    }
}
