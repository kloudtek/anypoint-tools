package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class APISpec {
    @JsonProperty
    private String groupId;
    @JsonProperty
    private String assetId;
    @JsonProperty
    private String version;
    @JsonProperty
    private String productAPIVersion;
    @JsonProperty
    private String name;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProductAPIVersion() {
        return productAPIVersion;
    }

    public void setProductAPIVersion(String productAPIVersion) {
        this.productAPIVersion = productAPIVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
