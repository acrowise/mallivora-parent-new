package com.mallivora.fabric.service.config;

import com.google.protobuf.Message;

public class StandardConfigValue {

    private String key;
    private Message value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Message getValue() {
        return value;
    }

    public void setValue(Message value) {
        this.value = value;
    }
}
