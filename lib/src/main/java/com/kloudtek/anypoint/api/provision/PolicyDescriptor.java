package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.policy.Policy;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PolicyDescriptorClientIdImpl.class, name = PolicyDescriptorClientIdImpl.TYPE)
})
public abstract class PolicyDescriptor {
    public abstract String getType();

    public abstract boolean update(Policy policy);

    public abstract Policy toPolicy(API apiVersion);
}
