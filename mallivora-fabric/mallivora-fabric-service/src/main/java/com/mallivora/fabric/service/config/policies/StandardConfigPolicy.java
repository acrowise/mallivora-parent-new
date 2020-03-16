package com.mallivora.fabric.service.config.policies;

import org.hyperledger.fabric.protos.common.Policies;

public class StandardConfigPolicy {

    private String key;
    private Policies.Policy value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Policies.Policy getValue() {
        return value;
    }

    public void setValue(Policies.Policy value) {
        this.value = value;
    }
}
