package com.kloudtek.anypoint.exchange;

/**
 * Created by JacksonGenerator on 6/26/18.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ExchangeAsset extends AnypointObject<Organization> {
    @JsonProperty("productAPIVersion")
    private String productAPIVersion;
    @JsonProperty("runtimeVersion")
    private String runtimeVersion;
    @JsonProperty("metadata")
    private AssetMetadata metadata;
    @JsonProperty("instances")
    private List<AssetInstance> instances;
    @JsonProperty("customFields")
    private List customFields;
    @JsonProperty("modifiedAt")
    private String modifiedAt;
    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("rating")
    private Integer rating;
    @JsonProperty("type")
    private String type;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("generated")
    private List generated;
    @JsonProperty("assetId")
    private String assetId;
    @JsonProperty("versionGroup")
    private String versionGroup;
    @JsonProperty("permissions")
    private List<String> permissions;
    @JsonProperty("isPublic")
    private Boolean isPublic;
    @JsonProperty("categories")
    private List categories;
    @JsonProperty("id")
    private String id;
    @JsonProperty("assetLink")
    private String assetLink;
    @JsonProperty("version")
    private String version;
    @JsonProperty("labels")
    private List labels;
    @JsonProperty("tags")
    private List<AssetTag> tags;
    @JsonProperty("dependencies")
    private List dependencies;
    @JsonProperty("createdBy")
    private AssetCreatedBy createdBy;
    @JsonProperty("versions")
    private List<AssetVersion> versions;
    @JsonProperty("name")
    private String name;
    @JsonProperty("files")
    private List<AssetFile> files;
    @JsonProperty("attributes")
    private List<AssetAttribute> attributes;
    @JsonProperty("status")
    private String status;
    @JsonProperty("numberOfRates")
    private Integer numberOfRates;

    public ExchangeAsset() {
    }

    public ExchangeAsset(Organization organization) {
        super(organization);
    }

    public AssetInstance findInstances(String name) throws NotFoundException {
        if( instances != null ) {
            Stream<AssetInstance> s = instances.stream().filter(i -> i.getEnvironmentId() != null);
            boolean namedInstance = !StringUtils.isEmpty(name);
            if(namedInstance) {
                s = s.filter( i -> i.getName().equalsIgnoreCase(name));
            }
            List<AssetInstance> ilist = s.collect(Collectors.toList());
            if(ilist.size() == 0 ) {
                return null;
            } else if( ilist.size() > 1) {
                if( namedInstance ) {
                    throw new NotFoundException("Found more than one instance for api "+groupId+":"+assetId+" while searching for instance "+name+
                            ". This is very unexpected as there shouldn't be instances with the same name");
                } else {
                    List<String> instanceNames = instances.stream().filter(i -> i.getEnvironmentId() != null)
                            .map(AssetInstance::getName).collect(Collectors.toList());
                    throw new NotFoundException("Found more than one instance for api "+groupId+":"+assetId+", please specify instance label: "+ instanceNames);
                }
            } else {
                return ilist.iterator().next();
            }
        }
        return null;
    }

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

    public List<AssetInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<AssetInstance> instances) {
        this.instances = instances;
    }

    public List getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List customFields) {
        this.customFields = customFields;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List getGenerated() {
        return generated;
    }

    public void setGenerated(List generated) {
        this.generated = generated;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
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

    public String getAssetLink() {
        return assetLink;
    }

    public void setAssetLink(String assetLink) {
        this.assetLink = assetLink;
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

    public List getDependencies() {
        return dependencies;
    }

    public void setDependencies(List dependencies) {
        this.dependencies = dependencies;
    }

    public AssetCreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AssetCreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    @NotNull
    public List<AssetVersion> getVersions() {
        return versions != null ? versions : Collections.emptyList();
    }

    public void setVersions(List<AssetVersion> versions) {
        this.versions = versions;
        if (versions != null) {
            for (AssetVersion assetVersion : versions) {
                assetVersion.setParent(this);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
