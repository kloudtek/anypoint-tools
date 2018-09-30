package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.api.SLATierLimits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SLATierDescriptor {
    @JsonProperty(required = true)
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private boolean autoApprove;
    @JsonProperty(required = true)
    private List<SLATierLimits> limits;

    public SLATierDescriptor(String name, String description, boolean autoApprove, SLATierLimits... limits) {
        this.name = name;
        this.description = description;
        this.autoApprove = autoApprove;
        if (limits != null) {
            this.limits = Arrays.asList(limits);
        }
    }

    public SLATierDescriptor(String name, boolean autoApprove, SLATierLimits... limits) {
        this(name, null, autoApprove, limits);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAutoApprove() {
        return autoApprove;
    }

    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    public List<SLATierLimits> getLimits() {
        return limits;
    }

    public void setLimits(List<SLATierLimits> limits) {
        this.limits = limits;
    }
}
