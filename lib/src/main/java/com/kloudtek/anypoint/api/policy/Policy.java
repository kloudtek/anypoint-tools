package com.kloudtek.anypoint.api.policy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.provision.PolicyDescriptor;

public class Policy extends AnypointObject<API> {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String policyTemplateId;
    @JsonProperty
    private String masterOrganizationId;
    @JsonProperty
    private String organizationId;
    @JsonProperty
    private Object configurationData;
    @JsonProperty
    private int order;
    @JsonProperty
    private boolean disabled;
    @JsonProperty
    private Object pointcutData;
    @JsonProperty
    private String groupId;
    @JsonProperty
    private String assetId;
    @JsonProperty
    private String assetVersion;
    @JsonProperty
    private String type;
    @JsonProperty
    private Integer apiId;

    public Policy() {
    }

    public Policy(API parent) {
        super(parent);
    }


    public void update(PolicyDescriptor policyDescriptor) throws HttpException {
        configurationData = policyDescriptor.getData();
        pointcutData = policyDescriptor.getPointcutData();
        httpHelper.httpPatch("https://anypoint.mulesoft.com/apimanager/api/v1/organizations/"+parent.getParent().getParent().getId()+"/environments/"+parent.getParent().getId()+"/apis/"+parent.getId()+"/policies/"+id,this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPolicyTemplateId() {
        return policyTemplateId;
    }

    public void setPolicyTemplateId(String policyTemplateId) {
        this.policyTemplateId = policyTemplateId;
    }

    public String getMasterOrganizationId() {
        return masterOrganizationId;
    }

    public void setMasterOrganizationId(String masterOrganizationId) {
        this.masterOrganizationId = masterOrganizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Object getConfigurationData() {
        return configurationData;
    }

    public void setConfigurationData(Object configurationData) {
        this.configurationData = configurationData;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Object getPointcutData() {
        return pointcutData;
    }

    public void setPointcutData(Object pointcutData) {
        this.pointcutData = pointcutData;
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

    public String getAssetVersion() {
        return assetVersion;
    }

    public void setAssetVersion(String assetVersion) {
        this.assetVersion = assetVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }
}
