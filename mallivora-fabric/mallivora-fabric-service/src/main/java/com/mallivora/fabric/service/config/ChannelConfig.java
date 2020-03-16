package com.mallivora.fabric.service.config;

import org.hyperledger.fabric.protos.common.Configuration;
import org.hyperledger.fabric.protos.msp.MspConfigPackage;
import org.hyperledger.fabric.protos.common.Policies;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChannelConfig {


    public static final String AnchorPeersKey = "AnchorPeers";
    // ApplicationGroupKey is the group name for the Application config
    public static final String ApplicationGroupKey = "Application";

    // ACLsKey is the name of the ACLs config
    public static final String ACLsKey = "ACLs";

    public static final String MSP_KEY = "MSP";
    // ReadersPolicyKey is the key used for the read policy
    public static final String ReadersPolicyKey = "Readers";
    // WritersPolicyKey is the key used for the read policy
    public static final String WritersPolicyKey = "Writers";
    // AdminsPolicyKey is the key used for the read policy
    public static final String AdminsPolicyKey = "Admins";
    public static final String defaultHashingAlgorithm = "SHA256";
    public static final Integer defaultBlockDataHashingStructureWidth = 1 << 32 - 1;

    // ConsortiumKey is the key for the cb.ConfigValue for the Consortium message
    public static final String ConsortiumKey = "Consortium";
    // HashingAlgorithmKey is the cb.ConfigItem type key name for the HashingAlgorithm message
    public static final String HashingAlgorithmKey = "HashingAlgorithm";
    // BlockDataHashingStructureKey is the cb.ConfigItem type key name for the BlockDataHashingStructure message
    public static final String BlockDataHashingStructureKey = "BlockDataHashingStructure";
    // OrdererAddressesKey is the cb.ConfigItem type key name for the OrdererAddresses message
    public static final String OrdererAddressesKey = "OrdererAddresses";
    // GroupKey is the name of the channel group
    public static final String ChannelGroupKey = "Channel";
    // CapabilitiesKey is the name of the key which refers to capabilities, it appears at the channel,
    // application, and orderer levels and this constant is used for all three.
    public static final String CapabilitiesKey = "Capabilities";

    public static StandardConfigValue hashingAlgorithmValue() {
        return new StandardConfigValue() {
            {
                setKey(HashingAlgorithmKey);
                setValue(Configuration.HashingAlgorithm.newBuilder().setName(defaultHashingAlgorithm).build());
            }
        };
    }

    public static StandardConfigValue blockDataHashingStructureValue() {
        return new StandardConfigValue() {
            {
                setKey(BlockDataHashingStructureKey);
                setValue(Configuration.BlockDataHashingStructure.newBuilder()
                    .setWidth(defaultBlockDataHashingStructureWidth * 2 - 1).build());
            }
        };
    }

    public static StandardConfigValue ordererAddressesValue(String[] addresses) {
        return new StandardConfigValue() {
            {
                setKey(OrdererAddressesKey);
                setValue(Configuration.OrdererAddresses.newBuilder().addAllAddresses(Arrays.asList(addresses)).build());
            }
        };
    }

    public static StandardConfigValue consortiumValue(String name) {
        return new StandardConfigValue() {
            {
                setKey(ConsortiumKey);
                setValue(Configuration.Consortium.newBuilder().setName(name).build());
            }
        };
    }

    public static StandardConfigValue capabilitiesValue(Map<String, Boolean> capabilities) {
        Configuration.Capabilities.Builder c = Configuration.Capabilities.newBuilder();
        for (String key : capabilities.keySet()) {
            if (!capabilities.get(key)) {
                continue;
            }
            c.putCapabilities(key, Configuration.Capability.newBuilder().build());
        }
        return new StandardConfigValue() {
            {
                setKey(CapabilitiesKey);
                setValue(c.build());
            }
        };
    }

    public static StandardConfigValue batchSizeValue(int maxMessages, int absoluteMaxBytes, int preferredMaxBytes) {
        return new StandardConfigValue() {
            {
                setKey(Orderer.BATCH_SIZE_KEY);
                setValue(org.hyperledger.fabric.protos.orderer.Configuration.BatchSize.newBuilder()
                    .setAbsoluteMaxBytes(absoluteMaxBytes).setMaxMessageCount(maxMessages)
                    .setPreferredMaxBytes(preferredMaxBytes).build());
            }
        };
    }

    public static StandardConfigValue batchTimeoutValue(String timeout) {
        return new StandardConfigValue() {
            {
                setKey(Orderer.BATCH_TIMEOUT_KEY);
                setValue(org.hyperledger.fabric.protos.orderer.Configuration.BatchTimeout.newBuilder()
                    .setTimeout(timeout).build());
            }
        };
    }

    public static StandardConfigValue channelRestrictionsValue(Long maxChannelCount) {
        return new StandardConfigValue() {
            {
                setKey(Orderer.CHANNEL_RESTRICTIONS_KEY);
                setValue(org.hyperledger.fabric.protos.orderer.Configuration.ChannelRestrictions.newBuilder()
                    .setMaxCount(maxChannelCount).build());
            }
        };
    }

    public static StandardConfigValue kafkaBrokersValue(String[] brokers) {
        return new StandardConfigValue() {
            {
                setKey(Orderer.KAFKA_BROKERS_KEY);
                setValue(org.hyperledger.fabric.protos.orderer.Configuration.KafkaBrokers.newBuilder()
                    .addAllBrokers(Arrays.asList(brokers)).build());
            }
        };
    }

    public static StandardConfigValue consensusTypeValue(String consensusType,
        org.hyperledger.fabric.protos.orderer.etcdraft.Configuration.ConfigMetadata metadata) {
        return new StandardConfigValue() {
            {
                setKey(Orderer.CONSENSUS_TYPE_KEY);
                setValue(org.hyperledger.fabric.protos.orderer.Configuration.ConsensusType.newBuilder()
                    .setType(consensusType).setMetadata(metadata.toByteString()).build());
            }
        };
    }

    public static StandardConfigValue MSPValue(MspConfigPackage.MSPConfig mspdef) {
        return new StandardConfigValue() {
            {
                setKey(Organization.MSP_KEY);
                setValue(mspdef);
            }
        };
    };

    public static StandardConfigValue endpointValue(List<String> addresses){
        return new StandardConfigValue(){
            {
                setKey(Orderer.ENDPOINTS_KEY);
                setValue(Configuration.OrdererAddresses.newBuilder().addAllAddresses(addresses).build());
            }
        };
    }

    public static StandardConfigValue getACLValues(Map<String,String> acls) {
        return new StandardConfigValue() {
            {
                setKey(Application.ACLS_KEY);
                org.hyperledger.fabric.protos.peer.Configuration.ACLs.Builder aclsbuilder = org.hyperledger.fabric.protos.peer.Configuration.ACLs.newBuilder();
                acls.forEach((k,v) -> {
                    aclsbuilder.putAcls(k,org.hyperledger.fabric.protos.peer.Configuration.APIResource.newBuilder().setPolicyRef(v).build());
                });
                setValue(aclsbuilder.build());
            }
        };
    }

    public static StandardConfigValue getAnchorPeersValue(List<org.hyperledger.fabric.protos.peer.Configuration.AnchorPeer> anchorPeers) {
        return new StandardConfigValue(){
            {
                setKey(Application.ANCHOR_PEERS_KEY);
                setValue(org.hyperledger.fabric.protos.peer.Configuration.AnchorPeers.newBuilder().addAllAnchorPeers(anchorPeers).build());
            }
        };
    }

    public static StandardConfigValue getChannelCreationPolicyValue(Policies.Policy policy) {
        return new StandardConfigValue(){
            {
                setKey(Consortium.CHANNEL_CREATION_POLICY_KEY);
                setValue(policy);
            }
        };
    }

    public static void main(String[] args) {
        Double pow = Math.pow(2, 32);
        int value = pow.intValue();
        long aa = (long) (Math.pow(2, 32) - 1);

        long bb = (1L<<32) - 1;

        System.out.println(defaultBlockDataHashingStructureWidth);
    }

}
