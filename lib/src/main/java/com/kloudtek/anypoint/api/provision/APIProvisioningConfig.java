package com.kloudtek.anypoint.api.provision;

import java.util.*;

public class APIProvisioningConfig {
    private String descriptorLocation = "anypoint.json";
    private Map<String, String> variables = new HashMap<>();
    private final List<String> accessedBy = new ArrayList<>();
    private String apiLabel;

    public APIProvisioningConfig() {
    }

    public APIProvisioningConfig(Map<String, String> variables, List<String> accessedBy) {
        this.variables.putAll(variables);
        if (accessedBy != null) {
            this.accessedBy.addAll(accessedBy);
        }
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }

    public List<String> getAccessedBy() {
        return accessedBy;
    }

    public void addAccessedBy(String clientAppName) {
        accessedBy.add(clientAppName);
    }

    public String getDescriptorLocation() {
        return descriptorLocation;
    }

    public void setDescriptorLocation(String descriptorLocation) {
        this.descriptorLocation = descriptorLocation;
    }

    public void addVariable(String key, String value) {
        variables.put(key, value);
    }

    public String getApiLabel() {
        return apiLabel;
    }

    public void setApiLabel(String apiLabel) {
        this.apiLabel = apiLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof APIProvisioningConfig)) return false;
        APIProvisioningConfig that = (APIProvisioningConfig) o;
        return Objects.equals(descriptorLocation, that.descriptorLocation) &&
                Objects.equals(variables, that.variables) &&
                Objects.equals(accessedBy, that.accessedBy) &&
                Objects.equals(apiLabel, that.apiLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorLocation, variables, accessedBy, apiLabel);
    }

    @Override
    public String toString() {
        return "APIProvisioningConfig{" +
                "descriptorLocation='" + descriptorLocation + '\'' +
                ", variables=" + variables +
                ", accessedBy=" + accessedBy +
                ", apiLabel='" + apiLabel + '\'' +
                '}';
    }
}
