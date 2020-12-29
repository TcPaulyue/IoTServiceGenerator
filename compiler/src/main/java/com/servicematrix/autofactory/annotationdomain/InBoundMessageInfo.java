package com.servicematrix.autofactory.annotationdomain;

import java.util.List;

public class InBoundMessageInfo {
    String message;
    List<String> codes;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public InBoundMessageInfo(String message, List<String> codes) {
        this.message = message;
        this.codes = codes;
    }
}
