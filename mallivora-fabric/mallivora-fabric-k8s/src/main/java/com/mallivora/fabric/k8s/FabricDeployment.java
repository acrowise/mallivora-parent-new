package com.mallivora.fabric.k8s;

import com.mallivora.fabric.k8s.orderer.OrdererDeployment;
import io.kubernetes.client.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FabricDeployment {


    V1ObjectMeta createMetadata(){
        return new V1ObjectMeta().name("orderer-k8s").labels(new HashMap<String, String>() {
            {
                put("app-k8s", "orderer-k8s");
            }
        });
    }

    protected abstract V1PodTemplateSpec createPodTemplateSpec();

    public static void main(String[] args) {
        FabricDeployment ordererDeployment = new OrdererDeployment();
        V1Deployment deployment = ordererDeployment.createDeployment();
    }


    protected abstract V1PodSpec createPodSpec(List<Container> containers);

    public V1Deployment createDeployment() {
        V1DeploymentSpec template = new V1DeploymentSpec().replicas(1).template(createPodTemplateSpec());
        V1Deployment deployment = new V1Deployment()
            .apiVersion("apps/v1")
            .kind("Deployment")
            .metadata(createMetadata())
            .spec(template);
        return deployment;
    }

    protected static List<V1Volume> createVolumes(List<LocalVolume> localVolumes) {
        List<V1Volume> volumes = new ArrayList<V1Volume>();

        for(LocalVolume localVolume : localVolumes) {
            volumes.add(new V1Volume().name(localVolume.getName()).hostPath(new V1HostPathVolumeSource().path(localVolume.getPath()).type(localVolume.getType())));
        }
        volumes.add(new V1Volume().name("current-dir").hostPath(new V1HostPathVolumeSource().path("/workspace").type("Directory")));
        volumes.add(new V1Volume().name("run").hostPath(new V1HostPathVolumeSource().path("/var/run").type("Directory")));
        return volumes;
    }

    public List<V1Container> createContainers(List<Container> containers) {
        List<V1Container> v1Containers = new ArrayList<V1Container>();
        for (Container container : containers) {
            v1Containers.add(createContainer(container.getName(), container.getImage(), container.getEnvVarsMap(),
                container.getVolumeMounts()));
        }
        return v1Containers;
    }

    private V1Container createContainer(String name, String image, Map<String, String> envVarsMap,
        List<Map<String, String>> volumeMounts) {
        return new V1ContainerBuilder().withName(name).withImage(image).withEnv(createEnvVars(createEnvVarMap()))
            .withVolumeMounts(createVolumeMounts(volumeMounts)).addToPorts(new V1ContainerPort().containerPort(80))
            .build();
    }

    protected abstract Map<String, String> createEnvVarMap();

    private static V1EnvVar createEnvVar(String name, String value) {
        return new V1EnvVarBuilder().withName(name).withValue(value).build();
    }

    private static List<V1EnvVar> createEnvVars(Map<String, String> envVarsMap) {
        List<V1EnvVar> envVars = new ArrayList<V1EnvVar>();
        for (Map.Entry<String, String> entry : envVarsMap.entrySet()) {
            envVars.add(createEnvVar(entry.getKey(), entry.getValue()));
        }
        return envVars;
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

    public static V1VolumeMount createVolumeMount(String name, String path) {
        return new V1VolumeMountBuilder().withName(name).withMountPath(path).build();
    }

    public static V1VolumeMount createVolumeMount(String name, String path, String subPath) {
        return new V1VolumeMountBuilder().withName(name).withMountPath(path).withNewSubPath(subPath).build();
    }
}
