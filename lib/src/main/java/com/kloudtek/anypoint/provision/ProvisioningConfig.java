package com.kloudtek.anypoint.provision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvisioningConfig)) return false;
        ProvisioningConfig that = (ProvisioningConfig) o;
        return legacyMode == that.legacyMode &&
                Objects.equals(descriptorLocation, that.descriptorLocation) &&
                Objects.equals(variables, that.variables) &&
                Objects.equals(accessedBy, that.accessedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorLocation, legacyMode, variables, accessedBy);
    }

    @Override
    public String toString() {
        return "ProvisioningConfig{" +
                "descriptorLocation='" + descriptorLocation + '\'' +
                ", legacyMode=" + legacyMode +
                ", variables=" + variables +
                ", accessedBy=" + accessedBy +
                '}';
    }
}
