package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class APIProvisioningConfig {
    @JsonProperty(defaultValue = "anypoint.json")
    private String descriptorLocation = "anypoint.json";
    @JsonProperty
    private Map<String, String> variables = new HashMap<>();
    @JsonProperty
    private final List<String> accessedBy = new ArrayList<>();
    @JsonProperty
    private String apiLabel;
    @JsonProperty(defaultValue = "true")
    private boolean autoApproveAPIAccessRequest = true;
    @JsonProperty(defaultValue = "true")
    private boolean injectApiId = true;
    @JsonProperty(defaultValue = "apiconfig.properties")
    private String injectApiIdFile = "apiconfig.properties";
    @JsonProperty(defaultValue = "anypoint.apiId")
    private String injectApiIdKey = "anypoint.apiId";

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

    public boolean isAutoApproveAPIAccessRequest() {
        return autoApproveAPIAccessRequest;
    }

    public void setAutoApproveAPIAccessRequest(boolean autoApproveAPIAccessRequest) {
        this.autoApproveAPIAccessRequest = autoApproveAPIAccessRequest;
    }

    public boolean isInjectApiId() {
        return injectApiId;
    }

    public void setInjectApiId(boolean injectApiId) {
        this.injectApiId = injectApiId;
    }

    public String getInjectApiIdFile() {
        return injectApiIdFile;
    }

    public void setInjectApiIdFile(String injectApiIdFile) {
        this.injectApiIdFile = injectApiIdFile;
    }

    public String getInjectApiIdKey() {
        return injectApiIdKey;
    }

    public void setInjectApiIdKey(String injectApiIdKey) {
        this.injectApiIdKey = injectApiIdKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof APIProvisioningConfig)) return false;
        APIProvisioningConfig that = (APIProvisioningConfig) o;
        return autoApproveAPIAccessRequest == that.autoApproveAPIAccessRequest &&
                injectApiId == that.injectApiId &&
                Objects.equals(descriptorLocation, that.descriptorLocation) &&
                Objects.equals(variables, that.variables) &&
                Objects.equals(accessedBy, that.accessedBy) &&
                Objects.equals(apiLabel, that.apiLabel) &&
                Objects.equals(injectApiIdFile, that.injectApiIdFile) &&
                Objects.equals(injectApiIdKey, that.injectApiIdKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorLocation, variables, accessedBy, apiLabel, autoApproveAPIAccessRequest, injectApiId, injectApiIdFile, injectApiIdKey);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", APIProvisioningConfig.class.getSimpleName() + "[", "]")
                .add("descriptorLocation='" + descriptorLocation + "'")
                .add("variables=" + variables)
                .add("accessedBy=" + accessedBy)
                .add("apiLabel='" + apiLabel + "'")
                .add("autoApproveAPIAccessRequest=" + autoApproveAPIAccessRequest)
                .add("injectApiId=" + injectApiId)
                .add("injectApiIdFile='" + injectApiIdFile + "'")
                .add("injectApiIdKey='" + injectApiIdKey + "'")
                .toString();
    }
}
