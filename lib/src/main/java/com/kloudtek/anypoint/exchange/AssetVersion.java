package com.kloudtek.anypoint.exchange;

/**
 * Created by JacksonGenerator on 6/26/18.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;

import java.util.List;


public class AssetVersion extends AnypointObject<ExchangeAsset> {
    @JsonProperty("productAPIVersion")
    private String productAPIVersion;
    @JsonProperty("runtimeVersion")
    private String runtimeVersion;
    @JsonProperty("metadata")
    private AssetMetadata metadata;
    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("assetLink")
    private String assetLink;
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;
    @JsonProperty("tags")
    private List<AssetTag> tags;
    @JsonProperty("labels")
    private List labels;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("assetId")
    private String assetId;
    @JsonProperty("versionGroup")
    private String versionGroup;
    @JsonProperty("name")
    private String name;
    @JsonProperty("isPublic")
    private Boolean isPublic;
    @JsonProperty("files")
    private List<AssetFile> files;
    @JsonProperty("attributes")
    private List<AssetAttribute> attributes;
    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private String status;

    public String getProductAPIVersion() {
        return productAPIVersion;
    }

    public void setProductAPIVersion(String productAPIVersion) {
        this.productAPIVersion = productAPIVersion;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public void setRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public AssetMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AssetMetadata metadata) {
        this.metadata = metadata;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAssetLink() {
        return assetLink;
    }

    public void setAssetLink(String assetLink) {
        this.assetLink = assetLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<AssetTag> getTags() {
        return tags;
    }

    public void setTags(List<AssetTag> tags) {
        this.tags = tags;
    }

    public List getLabels() {
        return labels;
    }

    public void setLabels(List labels) {
        this.labels = labels;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getVersionGroup() {
        return versionGroup;
    }

    public void setVersionGroup(String versionGroup) {
        this.versionGroup = versionGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public List<AssetFile> getFiles() {
        return files;
    }

    public void setFiles(List<AssetFile> files) {
        this.files = files;
    }

    public List<AssetAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AssetAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void delete() throws HttpException {
        client.getHttpHelper().httpDelete("https://anypoint.mulesoft.com/exchange/api/v1/organizations/"+parent.getParent().getId()+"/assets/"+parent.getGroupId()+"/"+parent.getAssetId()+"/"+version);
    }
}