/**
 * Project Name:mallivora-fabric-k8s File Name:OrdererDeployment.java Package Name:com.mallivora.fabric.k8s.orderer
 * Date:2020年3月16日上午11:11:40 Copyright (c) 2020, wangchao9@asiainfo.com All Rights Reserved.
 */

package com.mallivora.fabric.k8s.orderer;

import com.mallivora.fabric.k8s.Container;
import com.mallivora.fabric.k8s.FabricDeployment;
import io.kubernetes.client.models.*;

import static com.mallivora.fabric.k8s.FabricDeployment.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mallivora.fabric.k8s.FabricContainerEnvConstant.*;
import static com.mallivora.fabric.k8s.FabricContainerEnvConstant.CORE_PEER_LOCALMSPID;

/**
 * ClassName:OrdererDeployment <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2020年3月16日 上午11:11:40 <br/>
 * 
 * @author 9527
 * @version
 * @since JDK 1.8
 * @see
 */
public class OrdererDeployment extends FabricDeployment {


    @Override
    protected V1PodTemplateSpec createPodTemplateSpec() {
        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
        v1PodTemplateSpec.setMetadata(new V1ObjectMeta().labels(createPodTemplateSpecMetaDataLabels("orderer1", "orderer")));
        v1PodTemplateSpec.setSpec(createPodSpec(null));
        return null;
    }

    @Override
    protected V1PodSpec createPodSpec(List<Container> containers) {
        new V1PodSpec().containers(createContainers(containers)).volumes(createVolumes(null))
        return null;
    }


    private Map<String, String> createPodTemplateSpecMetaDataLabels(String orgName, String peerName) {
        return new HashMap<String, String>() {
            {
                put("app", "hyperledger");
                put("role", "orderer");
                put("org", orgName);
                put("name", peerName);
            }
        };
    }

    @Override
    protected Map<String, String> createEnvVarMap() {
        Map<String, String> evnVarMap = new HashMap<>();
        evnVarMap.put(FABRIC_LOGGING_SPEC_NAME, FABRIC_LOGGING_SPEC);
        evnVarMap.put(ORDERER_GENERAL_LISTENADDRESS_NAME, ORDERER_GENERAL_LISTENADDRESS);
        evnVarMap.put(ORDERER_GENERAL_GENESISMETHOD_NAME, ORDERER_GENERAL_GENESISMETHOD);
        evnVarMap.put(ORDERER_GENERAL_GENESISFILE_NAME, ORDERER_GENERAL_GENESISFILE);
        evnVarMap.put(ORDERER_GENERAL_LOCALMSPID_NAME, ORDERER_GENERAL_LOCALMSPID);
        evnVarMap.put(ORDERER_GENERAL_LOCALMSPDIR_NAME, ORDERER_GENERAL_LOCALMSPDIR);
        evnVarMap.put(ORDERER_GENERAL_TLS_ENABLED_NAME, ORDERER_GENERAL_TLS_ENABLED);
        evnVarMap.put(ORDERER_GENERAL_TLS_PRIVATEKEY_NAME, ORDERER_GENERAL_TLS_PRIVATEKEY);
        evnVarMap.put(ORDERER_GENERAL_TLS_CERTIFICATE_NAME, ORDERER_GENERAL_TLS_CERTIFICATE);
        evnVarMap.put(ORDERER_GENERAL_TLS_ROOTCAS_NAME, ORDERER_GENERAL_TLS_ROOTCAS);
        evnVarMap.put(ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE_NAME, ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE);
        evnVarMap.put(ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY_NAME, ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY);
        evnVarMap.put(ORDERER_GENERAL_CLUSTER_ROOTCAS_NAME, ORDERER_GENERAL_CLUSTER_ROOTCAS);
        return evnVarMap;
    }

}
