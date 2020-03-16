package com.mallivora.fabric.service.config.msp;

public class NodeOUs {

    private Boolean enable;
    private OrganizationalUnitIdentifiersConfiguration clientOUIdentifier;
    private OrganizationalUnitIdentifiersConfiguration peerOUIdentifier;
    private OrganizationalUnitIdentifiersConfiguration adminOUIdentifier;
    private OrganizationalUnitIdentifiersConfiguration ordererOUIdentifier;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public OrganizationalUnitIdentifiersConfiguration getClientOUIdentifier() {
        return clientOUIdentifier;
    }

    public void setClientOUIdentifier(OrganizationalUnitIdentifiersConfiguration clientOUIdentifier) {
        this.clientOUIdentifier = clientOUIdentifier;
    }

    public OrganizationalUnitIdentifiersConfiguration getPeerOUIdentifier() {
        return peerOUIdentifier;
    }

    public void setPeerOUIdentifier(OrganizationalUnitIdentifiersConfiguration peerOUIdentifier) {
        this.peerOUIdentifier = peerOUIdentifier;
    }

    public OrganizationalUnitIdentifiersConfiguration getAdminOUIdentifier() {
        return adminOUIdentifier;
    }

    public void setAdminOUIdentifier(OrganizationalUnitIdentifiersConfiguration adminOUIdentifier) {
        this.adminOUIdentifier = adminOUIdentifier;
    }

    public OrganizationalUnitIdentifiersConfiguration getOrdererOUIdentifier() {
        return ordererOUIdentifier;
    }

    public void setOrdererOUIdentifier(OrganizationalUnitIdentifiersConfiguration ordererOUIdentifier) {
        this.ordererOUIdentifier = ordererOUIdentifier;
    }

    class OrganizationalUnitIdentifiersConfiguration {
        private String certificate;
        private String organizationalUnitIdentifier;

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getOrganizationalUnitIdentifier() {
            return organizationalUnitIdentifier;
        }

        public void setOrganizationalUnitIdentifier(String organizationalUnitIdentifier) {
            this.organizationalUnitIdentifier = organizationalUnitIdentifier;
        }
    }

}
