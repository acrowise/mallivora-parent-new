package com.mallivora.fabric.service.config;

import org.hyperledger.fabric.protos.orderer.etcdraft.Configuration;

import java.util.Map;

public class Orderer {

    public static final String ORDERER_GROUP_KEY = "Orderer";
    public static final String CONSENSUS_TYPE_KEY = "ConsensusType";
    public static final String BATCH_SIZE_KEY = "BatchSize";
    public static final String BATCH_TIMEOUT_KEY = "BatchTimeout";
    public static final String CHANNEL_RESTRICTIONS_KEY = "ChannelRestrictions";
    public static final String KAFKA_BROKERS_KEY = "KafkaBrokers";
    public static final String ENDPOINTS_KEY = "EndpointsKey";

    private String ordererType;
    private String[] addresses;
    private Long batchTimeout;
    private BatchSize batchSize;
    private Kafka kafka;
    private Configuration.ConfigMetadata etcdRaft;
    private Organization[]  organizations;
    private Long maxChannels;
    private Map<String,Boolean> capabilities;
    private Map<String,Policy> policies;

    public String getOrdererType() {
        return ordererType;
    }

    public void setOrdererType(String ordererType) {
        this.ordererType = ordererType;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public Long getBatchTimeout() {
        return batchTimeout;
    }

    public void setBatchTimeout(Long batchTimeout) {
        this.batchTimeout = batchTimeout;
    }

    public BatchSize getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(BatchSize batchSize) {
        this.batchSize = batchSize;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public Configuration.ConfigMetadata getEtcdRaft() {
        return etcdRaft;
    }

    public void setEtcdRaft(Configuration.ConfigMetadata etcdRaft) {
        this.etcdRaft = etcdRaft;
    }

    public Organization[] getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Organization[] organizations) {
        this.organizations = organizations;
    }

    public Long getMaxChannels() {
        return maxChannels;
    }

    public void setMaxChannels(Long maxChannels) {
        this.maxChannels = maxChannels;
    }

    public Map<String, Boolean> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Boolean> capabilities) {
        this.capabilities = capabilities;
    }

    public Map<String, Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<String, Policy> policies) {
        this.policies = policies;
    }
}
