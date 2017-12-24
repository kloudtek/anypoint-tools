package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ProvisionedAPI extends ProvisionedAPIAccess {
    private List<ProvisionedAPIAccess> access;
    private boolean setupPortal = true;
    private String endpoint;
    private String description;
    private List<PolicyDescriptor> policies;

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
