package com.mallivora.fabric.k8s;

import io.kubernetes.client.proto.V1Apps;

public interface DeploymentFactory {

    V1Apps.Deployment createDeployment();

}
