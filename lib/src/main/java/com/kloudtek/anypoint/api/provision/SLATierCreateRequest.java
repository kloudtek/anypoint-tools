package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.SLATierLimits;

import java.util.ArrayList;
import java.util.List;

public class SLATierCreateRequest {
    @JsonProperty
    private int apiVersionId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String status = "ACTIVE";
    @JsonProperty
    private boolean autoApprove;
    @JsonProperty
    private List<SLATierLimits> limits = new ArrayList<>();

    public SLATierCreateRequest() {
    }

    public SLATierCreateRequest(API api, String name, String description, boolean autoApprove, List<SLATierLimits> limits) {
        apiVersionId = api.getId();
        this.name = name;
        this.description = description;
        this.autoApprove = autoApprove;
        this.limits = limits;
    }

    public int getApiVersionId() {
        return apiVersionId;
    }

    public void setApiVersionId(int apiVersionId) {
        this.apiVersionId = apiVersionId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
