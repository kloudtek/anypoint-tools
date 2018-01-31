package com.kloudtek.anypoint.provision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProvisioningConfig {
    private String descriptorLocation = "anypoint.json";
    private boolean legacyMode;
    private Map<String, String> provisioningParams;
    private final List<String> accessedBy = new ArrayList<>();

    public ProvisioningConfig(Map<String, String> provisioningParams, List<String> accessedBy) {
        this.provisioningParams = provisioningParams;
        if( accessedBy != null ) {
            this.accessedBy.addAll(accessedBy);
        }
    }

    public boolean isLegacyMode() {
        return legacyMode;
    }

    public void setLegacyMode(boolean legacyMode) {
        this.legacyMode = legacyMode;
    }

    public Map<String, String> getProvisioningParams() {
        return provisioningParams;
    }

    public void setProvisioningParams(Map<String, String> provisioningParams) {
        this.provisioningParams = provisioningParams;
    }

    public List<String> getAccessedBy() {
        return accessedBy;
    }

    public void addAccessedBy( String clientAppName ) {
        accessedBy.addAll(clientAppName);
    }

    public String getDescriptorLocation() {
        return descriptorLocation;
    }

    public void setDescriptorLocation(String descriptorLocation) {
        this.descriptorLocation = descriptorLocation;
    }
}
