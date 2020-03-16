package com.mallivora.fabric.service.config;

import java.util.List;
import java.util.Map;

public class Application {

    public static final String APPLICATION_GROUP_KEY = "Application";
    public static final String ACLS_KEY = "ACLs";
    public static final String ANCHOR_PEERS_KEY = "AnchorPeers";

    private List<Organization> organizations;
    private Map<String,Boolean> capabilities;
    private Resouces resources;
    private Map<String,Policy> policies;
    private Map<String,String> ACLs;

    public static String getApplicationGroupKey() {
        return APPLICATION_GROUP_KEY;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public Map<String, Boolean> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Boolean> capabilities) {
        this.capabilities = capabilities;
    }

    public Resouces getResources() {
        return resources;
    }

    public void setResources(Resouces resources) {
        this.resources = resources;
    }

    public Map<String, Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<String, Policy> policies) {
        this.policies = policies;
    }

    public Map<String, String> getACLs() {
        return ACLs;
    }

    public void setACLs(Map<String, String> ACLs) {
        this.ACLs = ACLs;
    }
}
