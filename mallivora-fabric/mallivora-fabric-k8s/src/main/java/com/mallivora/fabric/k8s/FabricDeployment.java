package com.mallivora.fabric.k8s;

import com.mallivora.fabric.k8s.peer.PeerDeployment;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Yaml;

import java.util.*;

public abstract class FabricDeployment {

    V1ObjectMeta createDeploymentMetadata(String namespace, String name) {
        return new V1ObjectMeta().name(name).namespace(namespace);
    }

    private V1PodTemplateSpec createPodTemplateSpec(String type, String orgName, String peerName,
        List<Container> containers) {
        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
        v1PodTemplateSpec.setMetadata(new V1ObjectMeta().labels(createSelectorLabels(type, orgName, peerName)));
        v1PodTemplateSpec.setSpec(createPodSpec(containers));
        return v1PodTemplateSpec;
    }

    private Map<String, String> createSelectorLabels(String type, String orgName, String peerName) {
        return new HashMap<String, String>() {
            {
                put("app", "hyperledger");
                put("role", type);
                put("org", orgName);
                put("name", peerName);
            }
        };
    }

    public static void main(String[] args) {

        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath("http://10.1.236.215:8080");
        FabricDeployment ordererDeployment = new PeerDeployment();
        String namespace = "test";
        V1Deployment deployment = ordererDeployment.createDeployment(namespace, "peer", "peer-org", "peer0", null);
        System.out.println(Yaml.dump(deployment));
        AppsV1Api v1Api = new AppsV1Api(client);
        try {
            V1Deployment namespacedDeployment =
                v1Api.createNamespacedDeployment(namespace, deployment, true, "true", null);
            System.out.println(namespacedDeployment);
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#createNamespace");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }

    private V1PodSpec createPodSpec(List<Container> containers) {
        return new V1PodSpec().containers(createContainers(containers)).volumes(createVolumes(null));
    }

    public V1Deployment createDeployment(String namespace, String type, String orgName, String peerName,
        List<Container> containers) {
        V1DeploymentSpec template =
            new V1DeploymentSpec().replicas(1).template(createPodTemplateSpec(type, orgName, peerName, containers))
                .selector(new V1LabelSelector().matchLabels(createSelectorLabels(type, orgName, peerName)));
        V1Deployment deployment = new V1Deployment().apiVersion("apps/v1").kind("Deployment")
            .metadata(createDeploymentMetadata(namespace, String.format("%s-%s", type, peerName))).spec(template);
        return deployment;
    }

    protected static List<V1Volume> createVolumes(List<LocalVolume> localVolumes) {
        List<V1Volume> volumes = new ArrayList<>();

        /*        for (LocalVolume localVolume : localVolumes) {
            volumes.add(new V1Volume().name(localVolume.getName())
                .hostPath(new V1HostPathVolumeSource().path(localVolume.getPath()).type(localVolume.getType())));
        }*/
        volumes.add(new V1Volume().name("current-dir")
            .hostPath(new V1HostPathVolumeSource().path("/workspace").type("Directory")));
        volumes
            .add(new V1Volume().name("run").hostPath(new V1HostPathVolumeSource().path("/var/run").type("Directory")));
        return volumes;
    }

    public List<V1Container> createContainers(List<Container> containers) {
        List<V1Container> v1Containers = new ArrayList<>();
        for (Container container : containers) {
            v1Containers.add(createContainer(container));
        }
        return v1Containers;
    }

    private V1Container createContainer(Container container) {
        return new V1ContainerBuilder().withName(container.getName()).withImage(container.getImage())
            .withEnv(createEnvVars(createEnvVarMap())).withVolumeMounts(createVolumeMounts(container.getVolumeMounts()))
            .withWorkingDir(container.getWorkingDir()).withCommand(container.getCommands())
            .addAllToPorts(creatContainerPorts(container.getPorts())).build();
    }

    private Collection<V1ContainerPort> creatContainerPorts(List<Integer> ports) {
        Collection collection = new ArrayList();
        for (Integer port : ports) {
            collection.add(new V1ContainerPort().containerPort(port));
        }
        return collection;
    }

    protected abstract Map<String, String> createEnvVarMap();

    private static V1EnvVar createEnvVar(String name, String value) {
        return new V1EnvVarBuilder().withName(name).withValue(value).build();
    }

    protected static List<V1EnvVar> createEnvVars(Map<String, String> envVarsMap) {
        List<V1EnvVar> envVars = new ArrayList<V1EnvVar>();
        for (Map.Entry<String, String> entry : envVarsMap.entrySet()) {
            envVars.add(createEnvVar(entry.getKey(), entry.getValue()));
        }
        return envVars;
    }

    protected abstract List<V1VolumeMount> createVolumeMounts(List<Map<String, String>> paths);

    public static V1VolumeMount createVolumeMount(String name, String path) {
        return new V1VolumeMountBuilder().withName(name).withMountPath(path).build();
    }

    public static V1VolumeMount createVolumeMount(String name, String path, String subPath) {
        return new V1VolumeMountBuilder().withName(name).withMountPath(path).withNewSubPath(subPath).build();
    }
}
