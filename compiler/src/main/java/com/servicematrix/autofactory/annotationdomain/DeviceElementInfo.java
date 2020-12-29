package com.servicematrix.autofactory.annotationdomain;

public class DeviceElementInfo {
    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String api;
    String type;

    public DeviceElementInfo(String api, String type) {
        this.api = api;
        this.type = type;
    }
}
