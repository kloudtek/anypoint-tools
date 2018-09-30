package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.kloudtek.anypoint.api.policy.Policy;

import java.util.Map;

public class PolicyDescriptorJsonImpl extends PolicyDescriptor {
    public static final String TYPE = "json";
    protected String policyTemplateId;
    protected String groupId;
    protected String assetId;
    protected String assetVersion;
    protected Map<String, Object> data;

    public PolicyDescriptorJsonImpl() {
    }

    public PolicyDescriptorJsonImpl(String groupId, String assetId, String assetVersion, Map<String, Object> data) {
        this.groupId = groupId;
        this.assetId = assetId;
        this.assetVersion = assetVersion;
        this.data = data;
    }

    @Override
    @JsonIgnore
    public String getType() {
        return TYPE;
    }

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

    @JsonRawValue
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @JsonProperty(required = true)
    public String getPolicyTemplateId() {
        return policyTemplateId;
    }

    public void setPolicyTemplateId(String policyTemplateId) {
        this.policyTemplateId = policyTemplateId;
    }

    @JsonProperty(required = true)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty(required = true)
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @JsonProperty(required = true)
    public String getAssetVersion() {
        return assetVersion;
    }

    public void setAssetVersion(String assetVersion) {
        this.assetVersion = assetVersion;
    }

    @Override
    public String toString() {
        return "PolicyDescriptorJsonImpl{" +
                "policyTemplateId='" + policyTemplateId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", assetId='" + assetId + '\'' +
                ", assetVersion='" + assetVersion + '\'' +
                ", data=" + data +
                ", pointcutData=" + pointcutData +
                "} " + super.toString();
    }
}
