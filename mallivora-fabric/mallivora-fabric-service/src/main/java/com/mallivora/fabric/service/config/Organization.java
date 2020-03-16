package com.mallivora.fabric.service.config;

import java.util.List;
import java.util.Map;

public class Organization {

    public static final String MSP_KEY = "MSP";

    private String name;
    private String id;
    private String MSPDir;
    private String MSPType;
    private Map<String,Policy> policies;
    private AnchorPeer[] anchorPeers;
    private List<String> ordererEndpoints;
    private String adminPrincipal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMSPDir() {
        return MSPDir;
    }

    public void setMSPDir(String MSPDir) {
        this.MSPDir = MSPDir;
    }

    public String getMSPType() {
        return MSPType;
    }

    public void setMSPType(String MSPType) {
        this.MSPType = MSPType;
    }

    public Map<String, Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<String, Policy> policies) {
        this.policies = policies;
    }

    public AnchorPeer[] getAnchorPeers() {
        return anchorPeers;
    }

    public void setAnchorPeers(AnchorPeer[] anchorPeers) {
        this.anchorPeers = anchorPeers;
    }

    public List<String> getOrdererEndpoints() {
        return ordererEndpoints;
    }

    public void setOrdererEndpoints(List<String> ordererEndpoints) {
        this.ordererEndpoints = ordererEndpoints;
    }

    public String getAdminPrincipal() {
        return adminPrincipal;
    }

    public void setAdminPrincipal(String adminPrincipal) {
        this.adminPrincipal = adminPrincipal;
    }
}
