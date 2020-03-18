/**
* Project Name:mallivora-fabric-k8s
* File Name:OrdererDeployment.java
* Package Name:com.mallivora.fabric.k8s.orderer
* Date:2020年3月16日上午11:11:40
* Copyright (c) 2020, wangchao9@asiainfo.com All Rights Reserved.
*/

package com.mallivora.fabric.k8s.orderer;

import com.mallivora.fabric.k8s.DeploymentFactory;
import io.kubernetes.client.proto.V1Apps;

/**
* ClassName:OrdererDeployment <br/>
* Function: TODO ADD FUNCTION. <br/>
* Reason: TODO ADD REASON. <br/>
* Date: 2020年3月16日 上午11:11:40 <br/>
* @author 9527
* @version
* @since JDK 1.8
* @see
*/
public class OrdererDeployment implements DeploymentFactory {

    public static void main(String[] args) {
    }

    @Override
    public V1Apps.Deployment createDeployment() {
        return null;
    }

}

