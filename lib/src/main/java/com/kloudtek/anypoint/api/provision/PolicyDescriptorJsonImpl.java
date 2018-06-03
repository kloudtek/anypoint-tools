package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.policy.ClientIdEnforcementPolicy;
import com.kloudtek.anypoint.api.policy.Policy;

import java.util.Objects;

public class PolicyDescriptorJsonImpl extends PolicyDescriptor {
    public static final String TYPE = "json";
    private Object data;
    private String policyTemplateId;
    private String groupId;
    private String assetId;
    private String assetVersion;

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean update(Policy policy) {
//        if (policy instanceof ClientIdEnforcementPolicy) {
//            boolean updated = false;
//            ClientIdEnforcementPolicy clientIdPolicy = (ClientIdEnforcementPolicy) policy;
//            if (!clientIdExpr.equals(clientIdPolicy.getClientIdExpression())) {
//                clientIdPolicy.setClientIdExpression(clientIdExpr);
//                updated = true;
//            }
//            if (!clientSecretExpr.equals(clientIdPolicy.getClientSecretExpression())) {
//                clientIdPolicy.setClientSecretExpression(clientSecretExpr);
//                updated = true;
//            }
//            return updated;
//        } else {
//            throw new IllegalArgumentException("Policy " + policy.getClass() + " isn't of clientIdSecret type, cannot update");
//        }
        return false;
    }

    @Override
    public Policy toPolicy(API apiVersion) {
        return null;
//        return new ClientIdEnforcementPolicy(apiVersion, clientIdExpr, clientSecretExpr);
    }

    @JsonRawValue
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @JsonProperty
    public String getPolicyTemplateId() {
        return policyTemplateId;
    }

    public void setPolicyTemplateId(String policyTemplateId) {
        this.policyTemplateId = policyTemplateId;
    }

    @JsonProperty
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @JsonProperty
    public String getAssetVersion() {
        return assetVersion;
    }

    public void setAssetVersion(String assetVersion) {
        this.assetVersion = assetVersion;
    }
}
