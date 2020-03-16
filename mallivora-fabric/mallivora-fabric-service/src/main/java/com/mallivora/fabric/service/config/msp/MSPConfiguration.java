package com.mallivora.fabric.service.config.msp;

public class MSPConfiguration {

    private NodeOUs.OrganizationalUnitIdentifiersConfiguration[] organizationalUnitIdentifiers;

    private NodeOUs nodeOUs;

    public NodeOUs.OrganizationalUnitIdentifiersConfiguration[] getOrganizationalUnitIdentifiers() {
        return organizationalUnitIdentifiers;
    }

    public void setOrganizationalUnitIdentifiers(NodeOUs.OrganizationalUnitIdentifiersConfiguration[] organizationalUnitIdentifiers) {
        this.organizationalUnitIdentifiers = organizationalUnitIdentifiers;
    }

    public NodeOUs getNodeOUs() {
        return nodeOUs;
    }

    public void setNodeOUs(NodeOUs nodeOUs) {
        this.nodeOUs = nodeOUs;
    }
}
