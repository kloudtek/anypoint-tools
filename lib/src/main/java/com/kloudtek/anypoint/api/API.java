package com.kloudtek.anypoint.api;

import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.policy.Policy;
import com.kloudtek.anypoint.api.provision.PolicyDescriptor;
import com.kloudtek.anypoint.api.provision.SLATierCreateRequest;
import com.kloudtek.anypoint.util.JsonHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            return "/apimanager/api/v1/organizations/" + parent.getParent().getId() + "/environments/" + parent.getId() + "/apis/" + id;
        } else if (organizationId != null && environmentId != null) {
            return "/apimanager/api/v1/organizations/" + organizationId + "/environments/" + environmentId + "/apis/" + id;
        } else {
            throw new IllegalStateException("Insufficient data available in api to build uri");
        }
    }

//    public API refresh() {
//
//    }

    public void createPolicy(PolicyDescriptor policyDescriptor) throws HttpException {
        Map<String, Object> reqMap = jsonHelper.buildJsonMap()
                .set("apiVersionId", id)
                .set("configurationData", policyDescriptor.getData())
                .set("pointcutData", policyDescriptor.getPointcutData())
                .set("policyTemplateId", policyDescriptor.getPolicyTemplateId())
                .set("groupId", policyDescriptor.getGroupId())
                .set("assetId", policyDescriptor.getAssetId())
                .set("assetVersion", policyDescriptor.getAssetVersion())
                .toMap();
        httpHelper.httpPost("/apimanager/api/v1/organizations/" + getParent().getParent().getId() + "/environments/" +
                getParent().getId() + "/apis/" + id + "/policies", reqMap);
    }

    public APIContract requestAPIAccess(ClientApplication clientApplication) throws HttpException {
        return clientApplication.requestAPIAccess(this);
    }

    public APIContract requestAPIAccess(ClientApplication clientApplication, SLATier tier) throws HttpException {
        return clientApplication.requestAPIAccess(this, tier);
    }

    public APIContract findContract(ClientApplication clientApplication) throws HttpException, NotFoundException {
        for (APIContract contract : findContracts()) {
            if (contract.getApplicationId().equals(clientApplication.getId())) {
                return contract;
            }
        }
        throw new NotFoundException();
    }

    public SLATierList findSLATiers() throws HttpException {
        return new SLATierList(this);
    }

    public SLATier findSLATier(@NotNull String name) throws HttpException, NotFoundException {
        for (SLATier slaTier : findSLATiers()) {
            if (slaTier.getName().equals(name)) {
                return slaTier;
            }
        }
        throw new NotFoundException();
    }

    public SLATier createSLATier(String name, String description, boolean autoApprove, List<SLATierLimits> limits) throws HttpException {
        String json = client.getHttpHelper().httpPost("https://anypoint.mulesoft.com/apimanager/api/v1/organizations/" +
                        parent.getParent().getId() + "/environments/" + parent.getId() + "/apis/" + id + "/tiers",
                new SLATierCreateRequest(this, name, description, autoApprove, limits));
        return jsonHelper.readJson(new SLATier(this), json);
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
        String json = environment.getClient().getHttpHelper().httpPost("/apimanager/api/v1/organizations/" + environment.getParent().getId() + "/environments/" + environment.getId() + "/apis", req);
        return environment.getClient().getJsonHelper().readJson(new API(environment), json);
    }

    public List<Policy> findPolicies() throws HttpException {
        String json = parent.getClient().getHttpHelper().httpGet("/apimanager/api/v1/organizations/" + getParent().getParent().getId() + "/environments/" + getParent().getId() + "/apis/" + id + "/policies?fullInfo=false");
        JsonHelper jsonHelper = parent.getClient().getJsonHelper();
        return jsonHelper.readJsonList(Policy.class, json, this);
    }

    public Policy findPolicyByAsset(String groupId, String assetId, String assetVersion) throws HttpException, NotFoundException {
        for (Policy policy : findPolicies()) {
            if (policy.getGroupId().equalsIgnoreCase(groupId) && policy.getAssetId().equalsIgnoreCase(assetId)
                    && policy.getAssetVersion().equalsIgnoreCase(assetVersion)) {
                return policy;
            }
        }
        throw new NotFoundException("Policy not found");
    }


    public APIContractList findContracts() throws HttpException {
        return new APIContractList(this);
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

    @Override
    public String toString() {
        return "API{" +
                "id=" + id +
                ", assetVersion='" + assetVersion + '\'' +
                ", productVersion='" + productVersion + '\'' +
                ", environmentId='" + environmentId + '\'' +
                ", instanceLabel='" + instanceLabel + '\'' +
                ", order=" + order +
                ", masterOrganizationId='" + masterOrganizationId + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", assetId='" + assetId + '\'' +
                ", autodiscoveryInstanceName='" + autodiscoveryInstanceName + '\'' +
                ", endpoint=" + endpoint +
                "} " + super.toString();
    }
}
