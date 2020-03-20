package com.mallivora.fabric.k8s;

public class FabricContainerEnvConstant {
    public static final String CORE_VM_ENDPOINT_NAME = "CORE_VM_ENDPOINT";
    public static final String CORE_VM_ENDPOINT = "unix:///host/var/run/docker.sock";
    public static final String CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE_NAME = "CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE";
    public static final String CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE = "byfn";
    public static final String FABRIC_LOGGING_SPEC_NAME = "FABRIC_LOGGING_SPEC";
    public static final String FABRIC_LOGGING_SPEC = "INFO";
    public static final String CORE_PEER_TLS_ENABLED_NAME = "CORE_PEER_TLS_ENABLED";
    public static final String CORE_PEER_TLS_ENABLED = "true";
    public static final String CORE_PEER_GOSSIP_USELEADERELECTION_NAME = "CORE_PEER_GOSSIP_USELEADERELECTION";
    public static final String CORE_PEER_GOSSIP_USELEADERELECTION = "true";
    public static final String CORE_PEER_GOSSIP_ORGLEADER_NAME = "CORE_PEER_GOSSIP_ORGLEADER";
    public static final String CORE_PEER_GOSSIP_ORGLEADER = "false";
    public static final String CORE_PEER_PROFILE_ENABLED_NAME = "CORE_PEER_PROFILE_ENABLED";
    public static final String CORE_PEER_PROFILE_ENABLED = "true";
    public static final String CORE_PEER_TLS_CERT_FILE_NAME = "CORE_PEER_TLS_CERT_FILE";
    public static final String CORE_PEER_TLS_CERT_FILE = "/etc/hyperledger/fabric/tls/server.crt";
    public static final String CORE_PEER_TLS_KEY_FILE_NAME = "CORE_PEER_TLS_KEY_FILE";
    public static final String CORE_PEER_TLS_KEY_FILE = "/etc/hyperledger/fabric/tls/server.key";
    public static final String CORE_PEER_TLS_ROOTCERT_FILE_NAME = "CORE_PEER_TLS_ROOTCERT_FILE";
    public static final String CORE_PEER_TLS_ROOTCERT_FILE = "/etc/hyperledger/fabric/tls/ca.crt";
    public static final String CORE_PEER_ID_NAME = "CORE_PEER_ID";
    public static final String CORE_PEER_ID = "peer0.org1.example.com";
    public static final String CORE_PEER_ADDRESS_NAME = "CORE_PEER_ADDRESS";
    public static final String CORE_PEER_ADDRESS = "peer0.org1.example.com:7051";
    public static final String CORE_PEER_LISTENADDRESS_NAME = "CORE_PEER_LISTENADDRESS";
    public static final String CORE_PEER_LISTENADDRESS = "0.0.0.0:7051";
    public static final String CORE_PEER_CHAINCODEADDRESS_NAME = "CORE_PEER_CHAINCODEADDRESS";
    public static final String CORE_PEER_CHAINCODEADDRESS = "peer0.org1.example.com:7052";
    public static final String CORE_PEER_CHAINCODELISTENADDRESS_NAME = "CORE_PEER_CHAINCODELISTENADDRESS";
    public static final String CORE_PEER_CHAINCODELISTENADDRESS = "0.0.0.0:7052";
    public static final String CORE_PEER_GOSSIP_BOOTSTRAP_NAME = "CORE_PEER_GOSSIP_BOOTSTRAP";
    public static final String CORE_PEER_GOSSIP_BOOTSTRAP = "peer1.org1.example.com:8051";
    public static final String CORE_PEER_GOSSIP_EXTERNALENDPOINT_NAME = "CORE_PEER_GOSSIP_EXTERNALENDPOINT";
    public static final String CORE_PEER_GOSSIP_EXTERNALENDPOINT = "peer0.org1.example.com:7051";
    public static final String CORE_PEER_LOCALMSPID_NAME = "CORE_PEER_LOCALMSPID_NAME";
    public static final String CORE_PEER_LOCALMSPID = "Org1MSP";

    public static final String ORDERER_GENERAL_LISTENADDRESS_NAME = "ORDERER_GENERAL_LISTENADDRESS";
    public static final String ORDERER_GENERAL_LISTENADDRESS = "0.0.0.0";
    public static final String ORDERER_GENERAL_GENESISMETHOD_NAME = "ORDERER_GENERAL_GENESISMETHOD";
    public static final String ORDERER_GENERAL_GENESISMETHOD = "file";
    public static final String ORDERER_GENERAL_GENESISFILE_NAME = "ORDERER_GENERAL_GENESISFILE";
    public static final String ORDERER_GENERAL_GENESISFILE = "/var/hyperledger/orderer/orderer.genesis.block";
    public static final String ORDERER_GENERAL_LOCALMSPID_NAME = "ORDERER_GENERAL_LOCALMSPID";
    public static final String ORDERER_GENERAL_LOCALMSPID = "OrdererMSP";
    public static final String ORDERER_GENERAL_LOCALMSPDIR_NAME = "ORDERER_GENERAL_LOCALMSPDIR";
    public static final String ORDERER_GENERAL_LOCALMSPDIR = "/var/hyperledger/orderer/msp";

    public static final String ORDERER_GENERAL_TLS_ENABLED_NAME = "ORDERER_GENERAL_TLS_ENABLED";
    public static final String ORDERER_GENERAL_TLS_ENABLED = "true";
    public static final String ORDERER_GENERAL_TLS_PRIVATEKEY_NAME = "ORDERER_GENERAL_TLS_PRIVATEKEY";
    public static final String ORDERER_GENERAL_TLS_PRIVATEKEY = "/var/hyperledger/orderer/tls/server.key";
    public static final String ORDERER_GENERAL_TLS_CERTIFICATE_NAME = "ORDERER_GENERAL_TLS_CERTIFICATE";
    public static final String ORDERER_GENERAL_TLS_CERTIFICATE = "/var/hyperledger/orderer/tls/server.crt";
    public static final String ORDERER_GENERAL_TLS_ROOTCAS_NAME = "ORDERER_GENERAL_TLS_ROOTCAS";
    public static final String ORDERER_GENERAL_TLS_ROOTCAS = "[/var/hyperledger/orderer/tls/ca.crt]";
    public static final String ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE_NAME = "ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE";
    public static final String ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE = "/var/hyperledger/orderer/tls/server.crt";
    public static final String ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY_NAME = "ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY";
    public static final String ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY = "/var/hyperledger/orderer/tls/server.key";
    public static final String ORDERER_GENERAL_CLUSTER_ROOTCAS_NAME = "ORDERER_GENERAL_CLUSTER_ROOTCAS";
    public static final String ORDERER_GENERAL_CLUSTER_ROOTCAS = "[/var/hyperledger/orderer/tls/ca.crt]";

}
