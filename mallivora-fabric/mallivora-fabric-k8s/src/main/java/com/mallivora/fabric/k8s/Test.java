package com.mallivora.fabric.k8s;

import com.google.common.reflect.TypeToken;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreApi;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) throws ApiException, IOException {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath("http://10.1.236.215:8080");

        AppsV1Api v1Api = new AppsV1Api(client);

        V1APIResourceList apiResources = v1Api.getAPIResources();
        CoreV1Api api = new CoreV1Api(client);
/*        V1PodList list =
            api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
        V1NamespaceList v1NamespaceList = api.listNamespace(null, "false", null, null, null, null, null, null, null);
        List<V1Namespace> items = v1NamespaceList.getItems();
        for(V1Namespace namespace: items) {
            System.out.println(namespace.getApiVersion() + " : " + namespace.getKind() + " : " + namespace.getMetadata().getName());
        }*/
/*        V1ObjectMeta test = new V1ObjectMeta().generateName("test2").labels(new HashMap<String,String>(){
            {put("name","dev");}
        });
        V1Namespace body = new V1Namespace().metadata(test).apiVersion("v1").kind("Namespace"); // V1Namespace |
        String pretty = "false"; // String | If 'true', then the output is pretty printed.
        String dryRun = "All"; // String | When present, indicates that modifications should not be persisted. An invalid or unrecognized dryRun directive will result in an error response and no further processing of the request. Valid values are: - All: all dry run stages will be processed
        System.out.println(Yaml.dump(body));
        try {
            V1Namespace result = api.createNamespace(body, false, pretty, null);
            System.out.println("------------");
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#createNamespace");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }*/

        V1ConfigMap configMap = new V1ConfigMap().data(new HashMap<String, String>() {
            {
                put("common-env-value", "true");
            }
        }).kind("ConfigMap").metadata(new V1ObjectMeta().name("fabric-common-1"));
        V1ConfigMap test28wdgw = api.createNamespacedConfigMap("test28wdgw", configMap, false, "false", null);
        System.out.println(test28wdgw);
    }
}
