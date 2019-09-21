package com.kloudtek.anypoint.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class AssetInstance {
    @JsonProperty
    private String versionGroup;
    @JsonProperty
    private String organizationId;
    @JsonProperty
    private String id;
    @JsonProperty
    private String groupId;
    @JsonProperty
    private String assetId;
    @JsonProperty
    private String productAPIVersion;
    @JsonProperty
    private String version;
    @JsonProperty
    private String environmentId;
    @JsonProperty
    private String endpointUri;
    @JsonProperty
    private String name;
    @JsonProperty
    private boolean isPublic;
    @JsonProperty
    private String type;
    @JsonProperty
    private String fullname;
    @JsonProperty
    private String assetName;

    public AssetInstance() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AssetInstance.class.getSimpleName() + "[", "]")
                .add("versionGroup='" + versionGroup + "'")
                .add("organizationId='" + organizationId + "'")
                .add("id='" + id + "'")
                .add("groupId='" + groupId + "'")
                .add("assetId='" + assetId + "'")
                .add("productAPIVersion='" + productAPIVersion + "'")
                .add("version='" + version + "'")
                .add("environmentId='" + environmentId + "'")
                .add("endpointUri='" + endpointUri + "'")
                .add("name='" + name + "'")
                .add("isPublic=" + isPublic)
                .add("type='" + type + "'")
                .add("fullname='" + fullname + "'")
                .add("assetName='" + assetName + "'")
                .toString();
    }

    public String getVersionGroup() {
        return versionGroup;
    }

    public void setVersionGroup(String versionGroup) {
        this.versionGroup = versionGroup;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getProductAPIVersion() {
        return productAPIVersion;
    }

    public void setProductAPIVersion(String productAPIVersion) {
        this.productAPIVersion = productAPIVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
