package com.kloudtek.anypoint.provision;

import java.util.Map;

public class ProvisioningConfig {
    private String descriptorLocation = "anypoint.json";
    private boolean legacyMode;
    private Map<String, String> provisioningParams;

    public ProvisioningConfig(Map<String, String> provisioningParams) {
        this.provisioningParams = provisioningParams;
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

    public String getDescriptorLocation() {
        return descriptorLocation;
    }

    public void setDescriptorLocation(String descriptorLocation) {
        this.descriptorLocation = descriptorLocation;
    }
}
