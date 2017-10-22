package com.kloudtek.anypointlib.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypointlib.APIAccessContract;
import com.kloudtek.anypointlib.AnypointObject;
import com.kloudtek.anypointlib.HttpException;
import com.kloudtek.anypointlib.api.policy.Policy;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIVersion extends AnypointObject<API> {
    private String id;
    private String name;
    private String portalId;
    private boolean fullData;

    public APIVersion() {
    }

    public APIVersion(API api) {
        super(api);
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getPortalId() {
        return portalId;
    }

    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }

    @JsonIgnore
    public boolean isFullData() {
        return fullData;
    }

    public void setFullData(boolean fullData) {
        this.fullData = fullData;
    }

    public APIPortal createPortal() throws HttpException {
        return createPortal(null);
    }

    public APIPortal createPortal(@Nullable String name) throws HttpException {
        String json = httpHelper.httpPost(getUriPath() + "/portal", new HashMap<String, String>());
        APIPortal portal = jsonHelper.readJson(new APIPortal(this), json);
        return portal.updateName(name);
    }

    public APIVersion updatePortalId(String portalId) throws HttpException {
        String json = httpHelper.httpPatch(getUriPath(), jsonHelper.buildJsonMap().set("portalId", portalId).toMap());
        return jsonHelper.readJson(new APIVersion(parent), json);
    }

    public void updateEndpoint(String uri) throws HttpException {
        updateEndpoint("http", uri, null, null, null, null);
    }

    public void updateEndpoint(String type, String uri, Boolean isCloudHub, String proxyUri, String referencesUserDomain, String responseTimeout) throws HttpException {
        Map<String, Object> req = jsonHelper.buildJsonMap().set("type", type).set("uri", uri)
                .set("isCloudHub", isCloudHub).set("proxyUri", proxyUri)
                .set("referencesUserDomain", referencesUserDomain)
                .set("responseTimeout", responseTimeout).set("apiVersionId", id).toMap();
        httpHelper.httpPost(getUriPath() + "/endpoint", req);
    }

    String getUriPath() {
        return getParent().getUriPath() + "/versions/" + id;
    }

    @JsonIgnore
    public List<Policy> getPolicies(boolean fullInfo) throws HttpException {
        String json = httpHelper.httpGet(getUriPath() + "/policies?fullInfo=" + fullInfo);
        ArrayList<Policy> list = new ArrayList<>();
        for (JsonNode node : jsonHelper.readJsonTree(json)) {
            list.add(Policy.parseJson(this, jsonHelper.toJsonMap(node)));
        }
        return list;
    }

    public Policy createPolicy(Policy policy) throws HttpException {
        String json = httpHelper.httpPost(getUriPath() + "/policies", policy.toJson().toMap());
        return null;
    }

    public APIAccessContract requestAPIAccess(ClientApplication clientApplication) throws HttpException {
        return clientApplication.requestAPIAccess(this);
    }

    public APIAccessContract requestAPIAccess(ClientApplication clientApplication, String partyId, String partyName, boolean acceptedTerms) throws HttpException {
        return clientApplication.requestAPIAccess(this, partyId, partyName, acceptedTerms);
    }
}
