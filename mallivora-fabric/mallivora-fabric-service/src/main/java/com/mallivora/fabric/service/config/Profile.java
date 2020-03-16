package com.mallivora.fabric.service.config;

import java.util.Map;

public class Profile {
    private String Consortium;
    private Application Application;
    private Orderer Orderer;
    private Map<String,Consortium> Consortiums;
    private Map<String,Boolean> Capabilities;
    private Map<String,Policy> Policies;

    public String getConsortium() {
        return Consortium;
    }

    public void setConsortium(String consortium) {
        Consortium = consortium;
    }

    public Application getApplication() {
        return Application;
    }

    public void setApplication(Application application) {
        Application = application;
    }

    public Orderer getOrderer() {
        return Orderer;
    }

    public void setOrderer(Orderer orderer) {
        Orderer = orderer;
    }

    public Map<String, Consortium> getConsortiums() {
        return Consortiums;
    }

    public void setConsortiums(Map<String, Consortium> consortiums) {
        Consortiums = consortiums;
    }

    public Map<String, Boolean> getCapabilities() {
        return Capabilities;
    }

    public void setCapabilities(Map<String, Boolean> capabilities) {
        Capabilities = capabilities;
    }

    public Map<String, Policy> getPolicies() {
        return Policies;
    }

    public void setPolicies(Map<String, Policy> policies) {
        Policies = policies;
    }
}
