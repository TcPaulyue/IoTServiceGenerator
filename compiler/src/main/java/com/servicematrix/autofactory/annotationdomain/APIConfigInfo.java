package com.servicematrix.autofactory.annotationdomain;

import java.util.HashMap;
import java.util.Map;

public class APIConfigInfo {
    String url;
    String Authorization;
    String entity_id;

    public APIConfigInfo(String url, String authorization, String entity_id) {
        this.url = url;
        Authorization = authorization;
        this.entity_id = entity_id;
    }

    public APIConfigInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setAuthorization(String authorization) {
        Authorization = authorization;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }
}
