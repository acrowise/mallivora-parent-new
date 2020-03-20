package com.mallivora.fabric.k8s.peer;

import static com.mallivora.fabric.k8s.FabricContainerEnvConstant.*;
import com.mallivora.fabric.k8s.FabricDeployment;
import io.kubernetes.client.models.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeerDeployment extends FabricDeployment {

    protected List<V1VolumeMount> createVolumeMounts(List<Map<String, String>> paths) {
        List<V1VolumeMount> v1VolumeMounts = new ArrayList<V1VolumeMount>();
        v1VolumeMounts.add(createVolumeMount("current-dir", "/etc/hyperledger/fabric/tls",
            "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls"));
        v1VolumeMounts.add(createVolumeMount("run", "/host/var/run"));
        return v1VolumeMounts;
    }

    @Override
    protected Map<String, String> createEnvVarMap() {
        Map<String, String> evnVarMap = new HashMap<String, String>();
        evnVarMap.put(CORE_VM_ENDPOINT_NAME, CORE_VM_ENDPOINT);
        evnVarMap.put(CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE_NAME, CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE);
        evnVarMap.put(FABRIC_LOGGING_SPEC_NAME, FABRIC_LOGGING_SPEC);
        evnVarMap.put(CORE_PEER_TLS_ENABLED_NAME, CORE_PEER_TLS_ENABLED);
        evnVarMap.put(CORE_PEER_GOSSIP_USELEADERELECTION_NAME, CORE_PEER_GOSSIP_USELEADERELECTION);
        evnVarMap.put(CORE_PEER_GOSSIP_ORGLEADER_NAME, CORE_PEER_GOSSIP_ORGLEADER);
        evnVarMap.put(CORE_PEER_PROFILE_ENABLED_NAME, CORE_PEER_PROFILE_ENABLED);
        evnVarMap.put(CORE_PEER_TLS_CERT_FILE_NAME, CORE_PEER_TLS_CERT_FILE);
        evnVarMap.put(CORE_PEER_TLS_KEY_FILE_NAME, CORE_PEER_TLS_KEY_FILE);
        evnVarMap.put(CORE_PEER_TLS_ROOTCERT_FILE_NAME, CORE_PEER_TLS_ROOTCERT_FILE);
        evnVarMap.put(CORE_PEER_ID_NAME, CORE_PEER_ID);
        evnVarMap.put(CORE_PEER_ADDRESS_NAME, CORE_PEER_ADDRESS);
        evnVarMap.put(CORE_PEER_LISTENADDRESS_NAME, CORE_PEER_LISTENADDRESS);
        evnVarMap.put(CORE_PEER_CHAINCODEADDRESS_NAME, CORE_PEER_CHAINCODEADDRESS);
        evnVarMap.put(CORE_PEER_CHAINCODELISTENADDRESS_NAME, CORE_PEER_CHAINCODELISTENADDRESS);
        evnVarMap.put(CORE_PEER_GOSSIP_EXTERNALENDPOINT_NAME, CORE_PEER_GOSSIP_EXTERNALENDPOINT);
        evnVarMap.put(CORE_PEER_GOSSIP_BOOTSTRAP_NAME, CORE_PEER_GOSSIP_BOOTSTRAP);
        evnVarMap.put(CORE_PEER_LOCALMSPID_NAME, CORE_PEER_LOCALMSPID);
        return evnVarMap;
    }




}
