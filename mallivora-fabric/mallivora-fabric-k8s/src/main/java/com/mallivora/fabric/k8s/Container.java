package com.mallivora.fabric.k8s;

import java.util.List;
import java.util.Map;

public class Container {

    private String name;
    private String image;
    private Map<String, String> envVarsMap;
    private List<Map<String, String>> volumeMounts;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Map<String, String> getEnvVarsMap() {
        return envVarsMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setEnvVarsMap(Map<String, String> envVarsMap) {
        this.envVarsMap = envVarsMap;
    }

    public List<Map<String, String>> getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(List<Map<String, String>> volumeMounts) {
        this.volumeMounts = volumeMounts;
    }
}
