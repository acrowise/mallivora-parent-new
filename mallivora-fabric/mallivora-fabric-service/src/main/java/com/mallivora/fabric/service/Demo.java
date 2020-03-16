package com.mallivora.fabric.service;

import com.google.protobuf.ByteString;
import com.mallivora.fabric.service.config.AnchorPeer;
import com.mallivora.fabric.service.config.Application;
import com.mallivora.fabric.service.config.BatchSize;
import com.mallivora.fabric.service.config.Consortium;
import com.mallivora.fabric.service.config.Kafka;
import com.mallivora.fabric.service.config.Orderer;
import com.mallivora.fabric.service.config.Organization;
import com.mallivora.fabric.service.config.Policy;
import com.mallivora.fabric.service.config.Profile;

import org.hyperledger.fabric.protos.orderer.etcdraft.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {

    public static Profile newProfile() {
        Profile profile = new Profile();

        Map<String, Boolean> capabilites = new HashMap<String, Boolean>() {
            {
                put("V1_4_3", true);
                put("V1_3", false);
                put("V1_1", false);
            }
        };
        Map<String, Policy> policies = new HashMap<String, Policy>() {
            {
                put("Readers", newPolicy("ImplicitMeta", "ANY Readers"));
                put("Writers", newPolicy("ImplicitMeta", "ANY Writers"));
                put("Admins", newPolicy("ImplicitMeta", "MAJORITY Admins"));
            }
        };
        Map<String, Consortium> consortiums = new HashMap<String, Consortium>() {
            {
                put("SampleConsortium", newConsortiums());
            }
        };

        profile.setOrderer(newOrderer());
        profile.setConsortiums(consortiums);
        profile.setCapabilities(capabilites);
        profile.setPolicies(policies);
        return profile;
    }

    public static Profile newOutputChannelProfile() {
        Profile profile = new Profile();
        Map<String, Boolean> capabilites = new HashMap<String, Boolean>() {
            {
                put("V1_4_3", true);
                put("V1_3", false);
                put("V1_1", false);
            }
        };
        Map<String, Policy> policies = new HashMap<String, Policy>() {
            {
                put("Readers", newPolicy("ImplicitMeta", "ANY Readers"));
                put("Writers", newPolicy("ImplicitMeta", "ANY Writers"));
                put("Admins", newPolicy("ImplicitMeta", "MAJORITY Admins"));
            }
        };
        Map<String, Consortium> consortiums = new HashMap<String, Consortium>() {
            {
                put("SampleConsortium", newConsortiums());
            }
        };
        profile.setApplication(newApplication());
        profile.setConsortium("SampleConsortium");
        profile.setCapabilities(capabilites);
        profile.setPolicies(policies);
        return profile;
    }



    public static Orderer newOrderer() {
        Orderer orderer = new Orderer();
        orderer.setOrdererType("solo");
        orderer.setAddresses(new String[] {"orderer.example.com:7050"});
        orderer.setBatchTimeout(2000000000L);
        orderer.setBatchSize(new BatchSize() {
            {
                setMaxMessageCount(10);
                setAbsoluteMaxBytes(103809024);
                setPreferredMaxBytes(524288);
            }
        });
        orderer.setKafka(new Kafka() {
            {
                setBrokers(new String[] {"127.0.0.1:9092"});
            }
        });
        orderer.setOrganizations(new Organization[] {newOrdererOrg("OrdererOrg", "OrdererMSP",
                "E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\ordererOrganizations\\example.com\\msp")});
        orderer.setCapabilities(new HashMap<String, Boolean>() {
            {
                put("V1_4_2", true);
                put("V1_1", false);

            }
        });
        orderer.setMaxChannels(0L);
        orderer.setPolicies(new HashMap<String, Policy>() {
            {
                put("Readers", newPolicy("ImplicitMeta", "ANY Readers"));
                put("Writers", newPolicy("ImplicitMeta", "ANY Writers"));
                put("Admins", newPolicy("ImplicitMeta", "MAJORITY Admins"));
                put("BlockValidation", newPolicy("ImplicitMeta", "ANY Writers"));
            }
        });
        orderer.setEtcdRaft(newEtcdRaft());
        return orderer;
    }

    private static Application newApplication() {
        Application application = new Application();
        List<Organization> orgs = new ArrayList<Organization>() {
            {
                add(newOrganization("Org1MSP", "Org1MSP",
                        "E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org1.example.com\\msp"));
                add(newOrganization("Org2MSP", "Org2MSP",
                        "E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org2.example.com\\msp"));
            }
        };
        application.setOrganizations(orgs);
        application.setCapabilities(new HashMap<String, Boolean>() {
            {
                put("V1_4_2", true);
                put("V1_3", false);
                put("V1_2", false);
                put("V1_1", false);
            }
        });
        application.setPolicies(new HashMap<String, Policy>() {
            {
                Policy writersPolicy = newPolicy("ImplicitMeta", "ANY Readers");
                put("Readers", writersPolicy);
                put("Writers", newPolicy("ImplicitMeta", "ANY Writers"));
                put("Admins", newPolicy("ImplicitMeta", "MAJORITY Admins"));
            }
        });
        application.setACLs(new HashMap<>());
        return application;
    }

    public static Configuration.ConfigMetadata newEtcdRaft() {
        Configuration.Consenter consenter = Configuration.Consenter.newBuilder().setHost("orderer.example.com")
                .setPort(7051).setClientTlsCert(ByteString.EMPTY).setServerTlsCert(ByteString.EMPTY).build();

        return Configuration.ConfigMetadata.newBuilder().addConsenters(consenter).build();
    }

    public static Consortium newConsortiums() {
        List<Organization> list = new ArrayList<Organization>() {
            {
                add(newOrganization("Org1MSP", "Org1MSP",
                        "E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org2.example.com\\msp"));
                add(newOrganization("Org2MSP", "Org2MSP",
                        "E:\\watts\\git projects\\fabric-samples\\first-network\\crypto-config\\peerOrganizations\\org2.example.com\\msp"));
            }
        };
        Consortium consortium = new Consortium();
        consortium.setOrganizations(list);
        return consortium;
    }

    public static Policy newPolicy(String type, String rule) {
        Policy policy = new Policy();
        policy.setType(type);
        policy.setRule(rule);
        return policy;
    }

    public static Organization newOrganization(String name, String id, String mspDir) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setId(id);
        organization.setMSPDir(mspDir);
        organization.setMSPType("bccsp");
        Map<String, Policy> policies = new HashMap<String, Policy>() {
            {
                put("Admins", newPolicy("Signature", "OR('" + name + ".admin')"));
                put("Readers",
                        newPolicy("Signature", "OR('" + name + ".admin', '" + name + ".peer', '" + name + ".client')"));
                put("Signature", newPolicy("Signature", "OR('" + name + ".admin', '" + name + ".client')"));
            }
        };
        organization.setPolicies(policies);
        AnchorPeer anchorPeer = new AnchorPeer();
        anchorPeer.setHost("peer0.org1.example.com");
        anchorPeer.setPort(7051);
        organization.setAnchorPeers(new AnchorPeer[] {anchorPeer});
        organization.setAdminPrincipal("Role.ADMIN");
        organization.setOrdererEndpoints(new ArrayList<>());
        return organization;
    }

    public static Organization newOrdererOrg(String name, String id, String mspDir) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setId(id);
        organization.setMSPDir(mspDir);
        organization.setMSPType("bccsp");
        Map<String, Policy> policies = new HashMap<String, Policy>() {
            {
                put("Admins", newPolicy("Signature", "OR('OrdererMSP.peer')"));
                put("Readers", newPolicy("Signature", "OR('OrdererMSP.member')"));
                put("Writers", newPolicy("Signature", "OR('OrdererMSP.admin')"));
            }
        };
        organization.setPolicies(policies);
        AnchorPeer anchorPeer = new AnchorPeer();
        anchorPeer.setHost("peer0.org1.example.com");
        anchorPeer.setPort(7051);
        organization.setAnchorPeers(new AnchorPeer[] {anchorPeer});
        organization.setAdminPrincipal("Role.ADMIN");
        organization.setOrdererEndpoints(new ArrayList<>());
        return organization;
    }
}
