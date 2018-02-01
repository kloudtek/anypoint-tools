package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.api.APIVersion;
import com.kloudtek.anypoint.api.policy.ClientIdEnforcementPolicy;
import com.kloudtek.anypoint.api.policy.Policy;

import java.util.Objects;

public class PolicyDescriptorClientIdImpl extends PolicyDescriptor {
    public static final String TYPE = "client-id-enforcement";
    private String clientIdExpr;
    private String clientSecretExpr;

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean update(Policy policy) {
        if (policy instanceof ClientIdEnforcementPolicy) {
            boolean updated = false;
            ClientIdEnforcementPolicy clientIdPolicy = (ClientIdEnforcementPolicy) policy;
            if (!clientIdExpr.equals(clientIdPolicy.getClientIdExpression())) {
                clientIdPolicy.setClientIdExpression(clientIdExpr);
                updated = true;
            }
            if (!clientSecretExpr.equals(clientIdPolicy.getClientSecretExpression())) {
                clientIdPolicy.setClientSecretExpression(clientSecretExpr);
                updated = true;
            }
            return updated;
        } else {
            throw new IllegalArgumentException("Policy " + policy.getClass() + " isn't of clientIdSecret type, cannot update");
        }
    }

    @Override
    public Policy toPolicy(APIVersion apiVersion) {
        return new ClientIdEnforcementPolicy(apiVersion, clientIdExpr, clientSecretExpr);
    }

    @JsonProperty(required = true)
    public String getClientIdExpr() {
        return clientIdExpr;
    }

    public void setClientIdExpr(String clientIdExpr) {
        this.clientIdExpr = clientIdExpr;
    }

    @JsonProperty(required = true)
    public String getClientSecretExpr() {
        return clientSecretExpr;
    }

    public void setClientSecretExpr(String clientSecretExpr) {
        this.clientSecretExpr = clientSecretExpr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyDescriptorClientIdImpl)) return false;
        PolicyDescriptorClientIdImpl that = (PolicyDescriptorClientIdImpl) o;
        return Objects.equals(clientIdExpr, that.clientIdExpr) &&
                Objects.equals(clientSecretExpr, that.clientSecretExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientIdExpr, clientSecretExpr);
    }

    @Override
    public String toString() {
        return "PolicyDescriptorClientIdImpl{" +
                "clientIdExpr='" + clientIdExpr + '\'' +
                ", clientSecretExpr='" + clientSecretExpr + '\'' +
                "} " + super.toString();
    }
}
