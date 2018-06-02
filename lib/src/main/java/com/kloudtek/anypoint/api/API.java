package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.policy.Policy;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class API extends AnypointObject<Environment> {
    private static final Logger logger = LoggerFactory.getLogger(API.class);
    private int id;
    private String assetVersion;
    private String productVersion;
    private String environmentId;
    private String instanceLabel;
    private int order;
    private String masterOrganizationId;
    private String organizationId;
    private String groupId;
    private String assetId;
    private String autodiscoveryInstanceName;
    private APIEndpoint endpoint;

    public API() {
    }

    public API(Environment environment) {
        super(environment);
    }

    public String getUriPath() {
        if (parent != null) {
            return "/apimanager/api/v1/organizations/"+parent.getParent().getId()+"/environments/"+parent.getId()+"/apis/"+id;
        } else if (organizationId != null && environmentId != null) {
            return "/apimanager/api/v1/organizations/"+organizationId+"/environments/"+environmentId+"/apis/"+id;
        } else {
            throw new IllegalStateException("Insufficient data available in api to build uri");
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
        if (policy.getId() == null) {
            throw new IllegalArgumentException("Policy id musn't be null for update");
        }
        String json = httpHelper.httpPatch(new URLBuilder(getUriPath()).path("policies/" + policy.getId()).toString(), policy.toJson().toMap());
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

    public static API create(@NotNull Environment environment, @NotNull APISpec apiSpec, boolean mule4, @Nullable String endpointUrl, @Nullable String label) throws HttpException {
        HashMap<String, Object> req = new HashMap<>();
        req.put("instanceLabel", label);
        HashMap<String, Object> specMap = new HashMap<>();
        specMap.put("assetId", apiSpec.getAssetId());
        specMap.put("version", apiSpec.getVersion());
        specMap.put("groupId", apiSpec.getGroupId());
        req.put("spec", specMap);
        HashMap<String, Object> endpMap = new HashMap<>();
        endpMap.put("type", "rest-api");
        endpMap.put("uri", endpointUrl);
        endpMap.put("proxyUri", null);
        endpMap.put("isCloudHub", null);
        endpMap.put("deploymentType", "CH");
        endpMap.put("referencesUserDomain", null);
        endpMap.put("responseTimeout", null);
        endpMap.put("muleVersion4OrAbove", mule4);
        req.put("endpoint", endpMap);
        String json = environment.getClient().getHttpHelper().httpPost("https://anypoint.mulesoft.com/apimanager/api/v1/organizations/" + environment.getParent().getId()+"/environments/" + environment.getId() + "/apis", req);
        return environment.getClient().getJsonHelper().readJson(new API(environment), json);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssetVersion() {
        return assetVersion;
    }

    public void setAssetVersion(String assetVersion) {
        this.assetVersion = assetVersion;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getInstanceLabel() {
        return instanceLabel;
    }

    public void setInstanceLabel(String instanceLabel) {
        this.instanceLabel = instanceLabel;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public String getAutodiscoveryInstanceName() {
        return autodiscoveryInstanceName;
    }

    public void setAutodiscoveryInstanceName(String autodiscoveryInstanceName) {
        this.autodiscoveryInstanceName = autodiscoveryInstanceName;
    }

    public APIEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(APIEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}
