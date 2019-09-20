package com.kloudtek.anypoint.exchange;

/**
 * Created by JacksonGenerator on 6/26/18.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.Organization;

import java.io.IOException;
import java.util.List;


public class ExchangeAssetOverview extends AnypointObject<Organization> {
    @JsonProperty("productAPIVersion")
    private String productAPIVersion;
    @JsonProperty("runtimeVersion")
    private String runtimeVersion;
    @JsonProperty("customFields")
    private List customFields;
    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("assetLink")
    private String assetLink;
    @JsonProperty("rating")
    private Integer rating;
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;
    @JsonProperty("labels")
    private List labels;
    @JsonProperty("tags")
    private List<AssetTag> tags;
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
    @JsonProperty("categories")
    private List categories;
    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private String status;
    @JsonProperty("numberOfRates")
    private Integer numberOfRates;

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

    public List getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List customFields) {
        this.customFields = customFields;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
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

    public List getLabels() {
        return labels;
    }

    public void setLabels(List labels) {
        this.labels = labels;
    }

    public List<AssetTag> getTags() {
        return tags;
    }

    public void setTags(List<AssetTag> tags) {
        this.tags = tags;
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

    public List getCategories() {
        return categories;
    }

    public void setCategories(List categories) {
        this.categories = categories;
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

    public Integer getNumberOfRates() {
        return numberOfRates;
    }

    public void setNumberOfRates(Integer numberOfRates) {
        this.numberOfRates = numberOfRates;
    }

    @JsonIgnore
    public ExchangeAsset getAsset() throws HttpException {
        String json = client.getHttpHelper().httpGet("/exchange/api/v1/assets/" + groupId + "/" + assetId);
        return client.getJsonHelper().readJson(new ExchangeAsset(parent), json);
    }
}
