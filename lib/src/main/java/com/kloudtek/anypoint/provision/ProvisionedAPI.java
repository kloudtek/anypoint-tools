package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ProvisionedAPI extends ProvisionedAPIAccess {
    public static final String DEF_CREDKEY_NAME = "mule.client.id";
    public static final String MULE_CREDVAL_NAME = "mule.client.secret";
    private List<ProvisionedAPIAccess> access;
    private boolean setupPortal = true;
    private String endpoint;
    private String description;
    private List<PolicyDescriptor> policies;
    private String addCredsToPropertyFile;
    private String clientAppUrl;
    private String clientAppDescription;
    private String credIdPropertyName = DEF_CREDKEY_NAME;
    private String credSecretPropertyName = MULE_CREDVAL_NAME;
    private String credId;
    private String credSecret;

    @JsonProperty
    public List<ProvisionedAPIAccess> getAccess() {
        return access != null ? access : Collections.emptyList();
    }

    public void setAccess(List<ProvisionedAPIAccess> access) {
        this.access = access;
    }

    @JsonProperty(defaultValue = "true")
    public boolean isSetupPortal() {
        return setupPortal;
    }

    public void setSetupPortal(boolean setupPortal) {
        this.setupPortal = setupPortal;
    }

    @JsonProperty
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public List<PolicyDescriptor> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyDescriptor> policies) {
        this.policies = policies;
    }

    @JsonProperty
    public String getAddCredsToPropertyFile() {
        return addCredsToPropertyFile;
    }

    public void setAddCredsToPropertyFile(String addCredsToPropertyFile) {
        this.addCredsToPropertyFile = addCredsToPropertyFile;
    }

    @JsonProperty(defaultValue = DEF_CREDKEY_NAME)
    public String getCredIdPropertyName() {
        return credIdPropertyName;
    }

    public void setCredIdPropertyName(String credIdPropertyName) {
        this.credIdPropertyName = credIdPropertyName;
    }

    @JsonProperty(defaultValue = MULE_CREDVAL_NAME)
    public String getCredSecretPropertyName() {
        return credSecretPropertyName;
    }

    public void setCredSecretPropertyName(String credSecretPropertyName) {
        this.credSecretPropertyName = credSecretPropertyName;
    }

    @JsonProperty
    public String getClientAppUrl() {
        return clientAppUrl;
    }

    public void setClientAppUrl(String clientAppUrl) {
        this.clientAppUrl = clientAppUrl;
    }

    @JsonProperty
    public String getClientAppDescription() {
        return clientAppDescription;
    }

    public void setClientAppDescription(String clientAppDescription) {
        this.clientAppDescription = clientAppDescription;
    }

    @JsonIgnore
    public String getCredId() {
        return credId;
    }

    public void setCredId(String credId) {
        this.credId = credId;
    }

    @JsonIgnore
    public String getCredSecret() {
        return credSecret;
    }

    public void setCredSecret(String credSecret) {
        this.credSecret = credSecret;
    }

    public void validate() throws InvalidAnypointDescriptorException {
        HashSet<String> types = new HashSet<>();
        if (policies != null) {
            for (PolicyDescriptor policy : policies) {
                if (types.contains(policy.getType())) {
                    throw new InvalidAnypointDescriptorException("There is more than one policy of type " + policy.getType());
                } else {
                    types.add(policy.getType());
                }
            }
        }
    }
}
