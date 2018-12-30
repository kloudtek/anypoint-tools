package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class APIProvisioningConfig {
    private static final String CONFIG_FILE = "config.properties";
    @JsonProperty(defaultValue = "anypoint.json")
    private String descriptorLocation = "anypoint.json";
    @JsonProperty
    private Map<String, String> variables = new HashMap<>();
    @JsonProperty
    private List<String> accessedBy = new ArrayList<>();
    @JsonProperty
    private String apiLabel;
    @JsonProperty(defaultValue = "true")
    private boolean autoApproveAPIAccessRequest = true;
    @JsonProperty(defaultValue = "true")
    private boolean injectApiId = true;
    @JsonProperty(defaultValue = CONFIG_FILE)
    private String configFile = CONFIG_FILE;
    @JsonProperty(defaultValue = "anypoint.api.id")
    private String injectApiIdKey = "anypoint.api.id";
    @JsonProperty(defaultValue = "true")
    private boolean injectClientIdSecret = true;
    @JsonProperty(defaultValue = "anypoint.api.client")
    private String injectClientIdSecretKey = "anypoint.api.client";
    @JsonProperty(defaultValue = "false")
    private boolean customLog4j;

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
        if( variables == null ) {
            variables = new HashMap<>();
        }
        variables.put(key, value);
    }

    public List<String> getAccessedBy() {
        return accessedBy;
    }

    public void addAccessedBy(String clientAppName) {
        accessedBy.add(clientAppName);
    }

    public void setAccessedBy(List<String> accessedBy) {
        this.accessedBy = accessedBy;
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

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getInjectApiIdKey() {
        return injectApiIdKey;
    }

    public void setInjectApiIdKey(String injectApiIdKey) {
        this.injectApiIdKey = injectApiIdKey;
    }

    public boolean isInjectClientIdSecret() {
        return injectClientIdSecret;
    }

    public void setInjectClientIdSecret(boolean injectClientIdSecret) {
        this.injectClientIdSecret = injectClientIdSecret;
    }

    public String getInjectClientIdSecretKey() {
        return injectClientIdSecretKey;
    }

    public void setInjectClientIdSecretKey(String injectClientIdSecretKey) {
        this.injectClientIdSecretKey = injectClientIdSecretKey;
    }

    public boolean isCustomLog4j() {
        return customLog4j;
    }

    public void setCustomLog4j(boolean customLog4j) {
        this.customLog4j = customLog4j;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof APIProvisioningConfig)) return false;
        APIProvisioningConfig that = (APIProvisioningConfig) o;
        return autoApproveAPIAccessRequest == that.autoApproveAPIAccessRequest &&
                injectApiId == that.injectApiId &&
                injectClientIdSecret == that.injectClientIdSecret &&
                Objects.equals(descriptorLocation, that.descriptorLocation) &&
                Objects.equals(variables, that.variables) &&
                Objects.equals(accessedBy, that.accessedBy) &&
                Objects.equals(apiLabel, that.apiLabel) &&
                Objects.equals(configFile, that.configFile) &&
                Objects.equals(injectApiIdKey, that.injectApiIdKey) &&
                Objects.equals(customLog4j, that.customLog4j) &&
                Objects.equals(injectClientIdSecretKey, that.injectClientIdSecretKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorLocation, variables, accessedBy, apiLabel, autoApproveAPIAccessRequest, injectApiId, configFile, injectApiIdKey, injectClientIdSecret, injectClientIdSecretKey, customLog4j);
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
                .add("configFile='" + configFile + "'")
                .add("injectApiIdKey='" + injectApiIdKey + "'")
                .add("injectClientIdSecret=" + injectClientIdSecret)
                .add("injectClientIdSecretKey='" + injectClientIdSecretKey + "'")
                .add("customLog4j='" + customLog4j + "'")
                .toString();
    }
}
