package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.policy.Policy;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PolicyDescriptorJsonImpl.class, name = PolicyDescriptorJsonImpl.TYPE)
})
public abstract class PolicyDescriptor {
    protected List<PolicyPointcut> pointcutData;

    public abstract String getType();

    @JsonProperty
    public List<PolicyPointcut> getPointcutData() {
        return pointcutData;
    }

    public void setPointcutData(List<PolicyPointcut> pointcutData) {
        this.pointcutData = pointcutData;
    }

    public abstract String getPolicyTemplateId();

    public abstract String getGroupId();

    public abstract String getAssetId();

    public abstract String getAssetVersion();

    public abstract Object getData();
}
