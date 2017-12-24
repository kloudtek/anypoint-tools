package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.policy.Policy;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIVersion extends AnypointObject<API> {
    private String id;
    private String name;
    private String portalId;
    private String apiId;
    private String organizationId;
    private boolean fullData;
    private APIVersionEndpoint endpoint;

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

    @JsonProperty
    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    @JsonProperty
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty
    public APIVersionEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(APIVersionEndpoint endpoint) {
        this.endpoint = endpoint;
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

    public String getUriPath() {
        if (parent != null) {
            return getParent().getUriPath() + "/versions/" + id;
        } else if (organizationId != null && apiId != null && id != null) {
            return "https://anypoint.mulesoft.com/apiplatform/repository/v2/organizations/" + organizationId + "/apis/" + apiId + "/versions/" + id;
        } else {
            throw new IllegalStateException("Insufficient data available in api version to build uri");
        }
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

    @JsonIgnore
    public HashMap<String, Policy> getPoliciesAsMap(boolean fullInfo) throws HttpException {
        HashMap<String, Policy> map = new HashMap<>();
        for (Policy policy : getPolicies(fullInfo)) {
            map.put(policy.getPolicyTemplateId(), policy);
        }
        return map;
    }

    public Policy createPolicy(Policy policy) throws HttpException {
        String json = httpHelper.httpPost(getUriPath() + "/policies", policy.toJson().toMap());
        return Policy.parseJson(this, jsonHelper.toJsonMap(json));
    }


    public Policy updatePolicy(Policy policy) throws HttpException {
        String json = httpHelper.httpPatch(new URLBuilder(parent.getUriPath()).path("policies/" + id).toString(), policy.toJson().toMap());
        return Policy.parseJson(this, jsonHelper.toJsonMap(json));
    }

    public APIAccessContract requestAPIAccess(ClientApplication clientApplication) throws HttpException {
        return clientApplication.requestAPIAccess(this);
    }

    public APIAccessContract requestAPIAccess(ClientApplication clientApplication, SLATier tier) throws HttpException {
        return clientApplication.requestAPIAccess(this, tier);
    }

    public APIAccessContract requestAPIAccess(ClientApplication clientApplication, SLATier tier, String partyId, String partyName, boolean acceptedTerms) throws HttpException {
        return clientApplication.requestAPIAccess(this, tier, partyId, partyName, acceptedTerms);
    }

    @JsonIgnore
    public String getNameOrId() {
        return name != null ? name : id;
    }

    public List<SLATier> getSLATiers() throws HttpException {
        String json = httpHelper.httpGet(getUriPath() + "/tiers");
        return jsonHelper.readJsonList(SLATier.class, json, this, "/tiers");
    }

    public SLATier getSLATier(String name) throws NotFoundException, HttpException {
        for (SLATier tier : getSLATiers()) {
            if (name.equalsIgnoreCase(tier.getName())) {
                return tier;
            }
        }
        throw new NotFoundException("Unable to find SLA Tier " + name);
    }
}
