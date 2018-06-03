package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ProvisionedAPI extends ProvisionedAPIAccess {
    public static final String DEF_CREDKEY_NAME = "mule.client.id";
    public static final String MULE_CREDVAL_NAME = "mule.client.secret";
    private List<ProvisionedAPIAccess> access;
    private String name;
    private String version;
    private String endpoint;
    private List<PolicyDescriptor> policies;
    private String clientAppUrl;
    private String clientAppDescription;
    private String clientAppName;
    private List<String> accessedBy;
    private Boolean mule4;

    @JsonProperty
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getMule4() {
        return mule4;
    }

    public void setMule4(Boolean mule4) {
        this.mule4 = mule4;
    }

    @JsonProperty
    public List<ProvisionedAPIAccess> getAccess() {
        return access != null ? access : Collections.emptyList();
    }

    public void setAccess(List<ProvisionedAPIAccess> access) {
        this.access = access;
    }

    @JsonProperty
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @JsonProperty
    public String getClientAppName() {
        return clientAppName;
    }

    public void setClientAppName(String clientAppName) {
        this.clientAppName = clientAppName;
    }

    @JsonProperty
    public List<PolicyDescriptor> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyDescriptor> policies) {
        this.policies = policies;
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

    @JsonProperty
    public List<String> getAccessedBy() {
        return accessedBy;
    }

    public void setAccessedBy(List<String> accessedBy) {
        this.accessedBy = accessedBy;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvisionedAPI)) return false;
        ProvisionedAPI that = (ProvisionedAPI) o;
        return Objects.equals(access, that.access) &&
                Objects.equals(endpoint, that.endpoint) &&
                Objects.equals(policies, that.policies) &&
                Objects.equals(clientAppUrl, that.clientAppUrl) &&
                Objects.equals(clientAppDescription, that.clientAppDescription) &&
                Objects.equals(clientAppName, that.clientAppName) &&
                Objects.equals(accessedBy, that.accessedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(access, endpoint, policies, clientAppUrl, clientAppDescription, clientAppName, accessedBy);
    }

    @Override
    public String toString() {
        return "ProvisionedAPI{" +
                "access=" + access +
                ", endpoint='" + endpoint + '\'' +
                ", policies=" + policies +
                ", clientAppUrl='" + clientAppUrl + '\'' +
                ", clientAppDescription='" + clientAppDescription + '\'' +
                ", clientAppName='" + clientAppName + '\'' +
                ", accessedBy=" + accessedBy +
                "} " + super.toString();
    }
}
