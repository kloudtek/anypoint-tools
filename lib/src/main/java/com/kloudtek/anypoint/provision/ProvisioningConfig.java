package com.kloudtek.anypoint.provision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProvisioningConfig {
    private String descriptorLocation = "anypoint.json";
    private boolean legacyMode;
    private Map<String, String> variables;
    private final List<String> accessedBy = new ArrayList<>();

    public ProvisioningConfig(Map<String, String> variables, List<String> accessedBy) {
        this.variables = variables;
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

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public List<String> getAccessedBy() {
        return accessedBy;
    }

    public void addAccessedBy( String clientAppName ) {
        accessedBy.add(clientAppName);
    }

    public String getDescriptorLocation() {
        return descriptorLocation;
    }

    public void setDescriptorLocation(String descriptorLocation) {
        this.descriptorLocation = descriptorLocation;
    }

    public void addVariable(String key, String value) {
        variables.put(key,value);
    }
}
