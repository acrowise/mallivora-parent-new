package com.mallivora.fabric.service.config;

import java.util.List;

public class Consortium {

    public static final String CONSORTIUM_GROUP_KEY = "Consortiums";
    public static final String CHANNEL_CREATION_POLICY_KEY = "ChannelCreationPolicy";

    private List<Organization> organizations;

    public static String getConsortiumGroupKey() {
        return CONSORTIUM_GROUP_KEY;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }
}
