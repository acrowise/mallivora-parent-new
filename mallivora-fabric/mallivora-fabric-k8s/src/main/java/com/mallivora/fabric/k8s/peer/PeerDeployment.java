package com.mallivora.fabric.k8s.peer;

import com.mallivora.fabric.k8s.Container;
import static com.mallivora.fabric.k8s.FabricContainerEnvConstant.*;
import static com.mallivora.fabric.k8s.FabricContainerEnvConstant.CORE_PEER_GOSSIP_BOOTSTRAP_NAME;

import com.mallivora.fabric.k8s.FabricDeployment;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Yaml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeerDeployment extends FabricDeployment {

    public static void main(String[] args) throws ApiException {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("peer");
        strings.add("node");
        strings.add("start");
        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath("http://10.1.236.215:8080");

        AppsV1Api v1Api = new AppsV1Api(client);

        V1APIResourceList apiResources = v1Api.getAPIResources();
        CoreV1Api api = new CoreV1Api(client);
        ArrayList<V1EnvFromSource> test = new ArrayList<V1EnvFromSource>() {
            {
                add(new V1EnvFromSource().configMapRef(new V1ConfigMapEnvSource().optional(true).name("test")));
            }
        };
        List<V1Container> list = new ArrayList<V1Container>() {
            {
                add(new V1Container().name("peer").image("hyperledger/fabric-peer:1.4.2")
                    .ports(new ArrayList<V1ContainerPort>() {
                        {
                            add(new V1ContainerPort().containerPort(7051));
                            add(new V1ContainerPort().containerPort(7052));
                            add(new V1ContainerPort().containerPort(7053));
                        }
                    }).env(createEnvVars(createPeerEnvVarMap())).command(strings)
                    .workingDir("/opt/gopath/src/github.com/hyperledger/fabric/peer")
                    .volumeMounts(createVolumeMounts(null)));
            }
        };

        V1Deployment deployment = new V1DeploymentBuilder().withKind("Deployment").withApiVersion("apps/v1")
            .withMetadata(new V1ObjectMeta().name("peer-k8s").labels(new HashMap<String, String>() {
                {
                    put("app-k8s", "peer-k8s");
                }
            }).namespace("test28wdgw")).withSpec(new V1DeploymentSpec().revisionHistoryLimit(10).replicas(1)
                .selector(new V1LabelSelector().matchLabels(new HashMap<String, String>() {
                    {
                        put("app-k8s", "peer-k8s");
                    }
                })).template(new V1PodTemplateSpec().metadata(new V1ObjectMeta().labels(new HashMap<String, String>() {
                    {
                        put("app-k8s", "peer-k8s");
                    }
                })).spec(new V1PodSpec().containers(list).volumes(createVolumes()))))
            .build();

        System.out.println(Yaml.dump(deployment));
        try {
            V1Deployment namespacedDeployment =
                v1Api.createNamespacedDeployment("test28wdgw", deployment, true, "true", null);
            System.out.println(namespacedDeployment);
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#createNamespace");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }

    }

    public static List<V1Volume> createVolumes() {
        List<V1Volume> list = new ArrayList<V1Volume>();
        list.add(new V1Volume().name("current-dir").hostPath(new V1HostPathVolumeSource().path("/workspace").type("Directory")));
        list.add(new V1Volume().name("run").hostPath(new V1HostPathVolumeSource().path("/var/run").type("Directory")));
        return list;
    }

    public static V1Container createContainer(String name, String image, Map<String, String> envVarsMap,
        List<Map<String, String>> volumeMounts) {
        return new V1ContainerBuilder().withName(name).withImage(image).withEnv(createEnvVars(envVarsMap))
            .withVolumeMounts(createVolumeMounts(volumeMounts)).addToPorts(new V1ContainerPort().containerPort(80))
            .build();
    }

    public static List<V1Container> createContainers(List<Container> containers) {
        List<V1Container> v1Containers = new ArrayList<V1Container>();
        for (Container container : containers) {
            v1Containers.add(createContainer(container.getName(), container.getImage(), container.getEnvVarsMap(),
                container.getVolumeMounts()));
        }
        return v1Containers;
    }

    public static V1VolumeMount createVolumeMount(String name, String path) {
        return new V1VolumeMountBuilder().withName(name).withMountPath(path).build();
    }

    public static V1VolumeMount createVolumeMount(String name, String path, String subPath) {
        return new V1VolumeMountBuilder().withName(name).withMountPath(path).withNewSubPath(subPath).build();
    }

    public static List<V1VolumeMount> createVolumeMounts(List<Map<String, String>> paths) {
        List<V1VolumeMount> v1VolumeMounts = new ArrayList<V1VolumeMount>();
        /*        for (Map<String, String> map : paths) {
            String name = map.get("name");
            String path = map.get("mountPath");
        }*/

/*        v1VolumeMounts.add(createVolumeMount("current-dir", "/etc/hyperledger/fabric/msp",
            "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp"));*/
        v1VolumeMounts.add(createVolumeMount("current-dir", "/etc/hyperledger/fabric/tls",
            "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls"));
        v1VolumeMounts.add(createVolumeMount("run", "/host/var/run"));
        return v1VolumeMounts;
    }

    public static V1EnvVar createEnvVar(String name, String value) {
        return new V1EnvVarBuilder().withName(name).withValue(value).build();
    }

    public static List<V1EnvVar> createEnvVars(Map<String, String> envVarsMap) {
        List<V1EnvVar> envVars = new ArrayList<V1EnvVar>();
        for (Map.Entry<String, String> entry : envVarsMap.entrySet()) {
            envVars.add(createEnvVar(entry.getKey(), entry.getValue()));
        }
        return envVars;
    }

    public static Map<String, String> createEnvVarMap(String kind) {
        if ("PEER".equals(kind)) {
            return createPeerEnvVarMap();
        } else if ("ORDERER".equals(kind)) {

        }
        return null;
    }

    public static Map<String, String> createPeerEnvVarMap() {
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

    @Override
    protected void createContainers() {

    }

    @Override
    protected V1ObjectMeta createMetadata() {
        return null;
    }

    @Override
    protected V1PodTemplateSpec createPodTemplateSpec() {
        return null;
    }

    V1PodSpec createV1PodSpec(List<Container> containers) {
        V1PodSpec v1PodSpec = new V1PodSpec().containers(createContainers(containers));
        return v1PodSpec;
    }

    protected V1PodTemplateSpec createPodTemplateSpec(String deploymentName, String orgName, String peerName,
        String namespace) {
        V1ObjectMeta metadata = new V1ObjectMeta().name(deploymentName).labels(new HashMap<String, String>() {
            {
                put("app", "hyperledger");
                put("role", "peer");
                put("org", orgName);
                put("name", peerName);
            }
        }).namespace(namespace).name(peerName);
        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec().metadata(metadata);
        return v1PodTemplateSpec;
    }
}
