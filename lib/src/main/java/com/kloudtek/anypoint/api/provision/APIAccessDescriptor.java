package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.api.API;

public class APIAccessDescriptor {
    private String groupId;
    private String assetId;
    private String assetVersion;
    private String label;
    private String slaTier;

    public APIAccessDescriptor() {
    }

    public APIAccessDescriptor(String groupId, String assetId, String assetVersion) {
        this(groupId, assetId, assetVersion, null, null);
    }

    public APIAccessDescriptor(String groupId, String assetId, String assetVersion, String label, String slaTier) {
        this.groupId = groupId;
        this.assetId = assetId;
        this.assetVersion = assetVersion;
        this.label = label;
        this.slaTier = slaTier;
    }

    public APIAccessDescriptor(API api) {
        this(api, null);
    }

    public APIAccessDescriptor(API api, String slaTier) {
        this(api.getGroupId(), api.getAssetId(), api.getAssetVersion(), api.getInstanceLabel(), slaTier);
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

    @JsonProperty(required = false)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty(required = false)
    public String getSlaTier() {
        return slaTier;
    }

    public void setSlaTier(String slaTier) {
        this.slaTier = slaTier;
    }
}
