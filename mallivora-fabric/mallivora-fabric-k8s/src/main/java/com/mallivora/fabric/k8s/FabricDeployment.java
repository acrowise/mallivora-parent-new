package com.mallivora.fabric.k8s;

import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentSpec;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PodTemplateSpec;

public abstract class FabricDeployment {

    protected abstract void createContainers();

    protected abstract V1ObjectMeta createMetadata();

    protected abstract V1PodTemplateSpec createPodTemplateSpec();

    public V1Deployment createDeployment() {

        V1DeploymentSpec template = new V1DeploymentSpec().replicas(1).template(createPodTemplateSpec());

        V1Deployment deployment = new V1Deployment()
            .apiVersion("apps/v1")
            .kind("Deployment")
            .metadata(createMetadata())
            .spec(template);
        return deployment;
    }

}
