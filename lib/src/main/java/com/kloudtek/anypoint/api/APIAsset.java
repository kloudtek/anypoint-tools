package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

//    @JsonIgnore
//    public String getUriPath() {
//        return "/apiplatform/repository/v2/organizations/" + parent.getId() + "/apis/" + id;
//    }
//
//    @JsonProperty
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @JsonProperty
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @JsonProperty
//    public List<API> getVersions() {
//        return versions;
//    }
//
//    public void setVersions(List<API> versions) {
//        this.versions = versions;
//        if (versions != null && client != null) {
//            for (API version : versions) {
//                version.setParent(this);
//            }
//        }
//    }
//
//    @JsonIgnore
//    public API getVersion(String version) throws NotFoundException, HttpException {
//        return getVersion(version, false);
//    }
//
//    @JsonIgnore
//    public API getVersion(String version, boolean fullData) throws NotFoundException, HttpException {
//        if (fullData) {
//            String json = httpHelper.httpGet(getUriPath() + "/versions/" + getVersionImpl(version).getId());
//            return jsonHelper.readJson(new API(this), json);
//        } else {
//            return getVersionImpl(version);
//        }
//    }
//
//    private API getVersionImpl(String version) throws NotFoundException {
//        for (API v : versions) {
//            if (version.equals(v.getName())) {
//                return v;
//            }
//        }
//        throw new NotFoundException("Unable to find version " + version);
//    }
//
//    @SuppressWarnings("unchecked")
//    @JsonIgnore
//    public List<APIPortal> getPortals() throws HttpException {
//        String json = httpHelper.httpGet(getUriPath() + "/portals");
//        ArrayList<APIPortal> apiPortals = new ArrayList<>();
//        for (JsonNode node : jsonHelper.readJsonTree(json)) {
//            apiPortals.add(jsonHelper.readJson(new APIPortal(), node));
//        }
//        return apiPortals;
//    }
//
//    @Override
//    public void setClient(AnypointClient client) {
//        super.setClient(client);
//        if (versions != null) {
//            for (API version : versions) {
//                version.setClient(client);
//            }
//        }
//    }
//
//    private void delete() throws HttpException {
//        httpHelper.httpDelete(getUriPath());
//    }
//
//    public API createVersion(String version) throws HttpException {
//        return createVersion(version, null, null);
//    }
//
//    public API createVersion(String version, String endpointUri, String description) throws HttpException {
//        Map<String, Object> req = jsonHelper.buildJsonMap().set("name", version).set("endpointUri", endpointUri)
//                .set("description", description).toMap();
//        String json = httpHelper.httpPost(getUriPath() + "/versions", req);
//        return jsonHelper.readJson(new API(this), json);
//    }

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
}
