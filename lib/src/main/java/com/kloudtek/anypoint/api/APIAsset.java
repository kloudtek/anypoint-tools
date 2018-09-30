package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;

import java.util.List;
import java.util.logging.Logger;

public class APIAsset extends AnypointObject<Environment> {
    private static final Logger logger = Logger.getLogger(APIAsset.class.getName());
    private int id;
    private String masterOrganizationId;
    private String organizationId;
    private String name;
    private String exchangeAssetName;
    private String groupId;
    private String assetId;
    private List<API> apis;

    public APIAsset() {
    }

    public APIAsset(Environment env) {
        super(env);
    }

    @Override
    public void setParent(Environment parent) {
        super.setParent(parent);
        if (apis != null) {
            for (API api : apis) {
                api.setParent(parent);
            }
        }
    }

    @JsonProperty
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty
    public String getMasterOrganizationId() {
        return masterOrganizationId;
    }

    public void setMasterOrganizationId(String masterOrganizationId) {
        this.masterOrganizationId = masterOrganizationId;
    }

    @JsonProperty
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getExchangeAssetName() {
        return exchangeAssetName;
    }

    public void setExchangeAssetName(String exchangeAssetName) {
        this.exchangeAssetName = exchangeAssetName;
    }

    @JsonProperty
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @JsonProperty
    public List<API> getApis() {
        return apis;
    }

    public void setApis(List<API> apis) {
        this.apis = apis;
    }

    public void delete() throws HttpException {
        for (API api : apis) {
            for (APIContract contract : api.findContracts()) {
                if (contract.isApproved()) {
                    contract.revokeAccess();
                }
                contract.delete();
            }
            httpHelper.httpDelete("/apimanager/api/v1/organizations/" + parent.getParent().getId() + "/environments/" + parent.getId() + "/apis/" + api.getId());
        }
    }
}
