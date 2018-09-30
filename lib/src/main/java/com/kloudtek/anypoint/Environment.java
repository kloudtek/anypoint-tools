package com.kloudtek.anypoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.APIAsset;
import com.kloudtek.anypoint.api.APIList;
import com.kloudtek.anypoint.api.APISpec;
import com.kloudtek.anypoint.cloudhub.CHMuleVersion;
import com.kloudtek.anypoint.cloudhub.CHRegion;
import com.kloudtek.anypoint.cloudhub.CHWorkerType;
import com.kloudtek.anypoint.runtime.CHApplication;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.anypoint.runtime.ServerGroup;
import com.kloudtek.util.UnexpectedException;
import org.apache.http.client.methods.HttpRequestBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kloudtek.util.StringUtils.isBlank;

public class Environment extends AnypointObject<Organization> {
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);
    private String id;
    private String name;
    private boolean production;
    private String type;
    private String clientId;

    public Environment() {
    }

    public Environment(Organization organization) {
        super(organization);
    }

    public Environment(Organization organization, String id) {
        super(organization);
        this.id = id;
    }

    @JsonIgnore
    public Organization getOrganization() {
        return parent;
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

    @JsonProperty("isProduction")
    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @NotNull
    public String getServerRegistrationKey() throws HttpException {
        String json = httpHelper.httpGet("/hybrid/api/v1/servers/registrationToken", this);
        return (String) jsonHelper.toJsonMap(json).get("data");
    }

    public List<Server> findAllServers() throws HttpException {
        String json = client.getHttpHelper().httpGet("/armui/api/v1/servers", this);
        ArrayList<Server> list = new ArrayList<>();
        for (JsonNode node : jsonHelper.readJsonTree(json).at("/data")) {
            JsonNode type = node.get("type");
            Server s;
            if (type.asText().equals("SERVER_GROUP")) {
                s = jsonHelper.readJson(new ServerGroup(this), node);
            } else {
                s = jsonHelper.readJson(new Server(this), node);
            }
            list.add(s);
        }
        return list;
    }

    public ServerGroup createServerGroup(String name, String... serverIds) throws HttpException {
        if (serverIds == null) {
            serverIds = new String[0];
        }
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        request.put("serverIds", serverIds);
        String json = httpHelper.httpPost("/hybrid/api/v1/serverGroups", request, this);
        return jsonHelper.readJson(new ServerGroup(this), json, "/data");
    }

    @NotNull
    public Server findServerByName(@NotNull String name) throws NotFoundException, HttpException {
        for (Server server : findAllServers()) {
            if (name.equals(server.getName())) {
                return server;
            }
        }
        throw new NotFoundException("Cannot find server : " + name);
    }

    public void addHeaders(HttpRequestBase method) {
        method.setHeader("X-ANYPNT-ORG-ID", parent.getId());
        method.setHeader("X-ANYPNT-ENV-ID", id);
    }

    public void delete() throws HttpException {
        for (Server server : findAllServers()) {
            server.delete();
        }
        httpHelper.httpDelete("/accounts/api/organizations/" + parent.getId() + "/environments/" + id);
        logger.info("Deleted environment " + id + " : " + name);
    }

    public Environment rename(String newName) throws HttpException {
        HashMap<String, String> req = new HashMap<>();
        req.put("id", id);
        req.put("name", newName);
        req.put("organizationId", parent.getId());
        String json = httpHelper.httpPut("/accounts/api/organizations/" + parent.getId() + "/environments/" + id, req);
        return jsonHelper.readJson(new Environment(parent), json);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", production=" + production +
                ", type='" + type + '\'' +
                ", clientId='" + clientId + '\'' +
                "} " + super.toString();
    }

    public APIList findAllAPIs() throws HttpException {
        return findAPIs(null);
    }

    public APIList findAPIs(String filter) throws HttpException {
        return new APIList(this, filter);
    }

    public API findAPIByExchangeAssetNameAndVersion(@NotNull String name, @NotNull String version) throws HttpException, NotFoundException {
        return findAPIByExchangeAssetNameAndVersion(name, version, null);
    }

    public API findAPIByExchangeAssetNameAndVersion(@NotNull String name, @NotNull String version, @Nullable String label) throws HttpException, NotFoundException {
        for (APIAsset asset : findAPIs(name)) {
            if (asset.getExchangeAssetName().equalsIgnoreCase(name)) {
                for (API api : asset.getApis()) {
                    if (api.getAssetVersion().equalsIgnoreCase(version) && (label == null || label.equalsIgnoreCase(api.getInstanceLabel()))) {
                        return api;
                    }
                }
            }
        }
        throw new NotFoundException("API " + name + " " + version + " not found");
    }

    public API findAPIByExchangeAsset(@NotNull String groupId, @NotNull String assetId, @NotNull String assetVersion) throws HttpException, NotFoundException {
        return findAPIByExchangeAsset(groupId, assetId, assetVersion, null);
    }

    public API findAPIByExchangeAsset(@NotNull String groupId, @NotNull String assetId, @NotNull String assetVersion, @Nullable String label) throws HttpException, NotFoundException {
        if (isBlank(groupId)) {
            throw new IllegalArgumentException("groupId missing (null or blank)");
        }
        if (isBlank(assetId)) {
            throw new IllegalArgumentException("assetId missing (null or blank)");
        }
        if (isBlank(assetVersion)) {
            throw new IllegalArgumentException("assetVersion missing (null or blank)");
        }
        for (APIAsset asset : findAllAPIs()) {
            if (asset.getGroupId().equalsIgnoreCase(groupId) && asset.getAssetId().equalsIgnoreCase(assetId)) {
                for (API api : asset.getApis()) {
                    if (api.getAssetVersion().equalsIgnoreCase(assetVersion) && (label == null || label.equalsIgnoreCase(api.getInstanceLabel()))) {
                        return api;
                    }
                }
            }
        }
        throw new NotFoundException("API based on exchange asset not found: groupId=" + groupId + ", assetId=" + assetId + ", assetVersion=" + assetVersion + ", label=" + label);
    }

    public CHApplication findCHApplicationByDomain(String domain) throws HttpException, NotFoundException {
        return CHApplication.find(this, domain);
    }

    public enum Type {
        DESIGN, SANDBOX, PRODUCTION
    }

    public API createAPI(@NotNull APISpec apiSpec, boolean mule4, @Nullable String endpointUrl, @Nullable String label) throws HttpException {
        return API.create(this, apiSpec, mule4, endpointUrl, label);
    }

    @SuppressWarnings("unchecked")
    public static List<Environment> findEnvironmentsByOrg(@NotNull AnypointClient client, @NotNull Organization organization) throws HttpException {
        String json = client.getHttpHelper().httpGet("/accounts/api/organizations/" + organization.getId() + "/environments");
        return client.getJsonHelper().readJsonList((Class<Environment>) organization.getEnvironmentClass(), json, organization, "/data");
    }

    @NotNull
    public static Environment fineEnvironmentByName(@NotNull String name, @NotNull AnypointClient client, @NotNull Organization organization) throws HttpException, NotFoundException {
        for (Environment environment : findEnvironmentsByOrg(client, organization)) {
            if (name.equals(environment.getName())) {
                return environment;
            }
        }
        throw new NotFoundException("Environment not found: " + name);
    }

    public String getNameOrId() {
        return name != null ? "(name) " + name : "(id) " + id;
    }

    public List<CHMuleVersion> findCHMuleVersions() throws HttpException {
        String json = client.getHttpHelper().httpGet("/cloudhub/api/mule-versions", this);
        return client.getJsonHelper().readJsonList(CHMuleVersion.class, json, this, "/data");
    }

    public CHMuleVersion findDefaultCHMuleVersion() throws HttpException {
        for (CHMuleVersion version : findCHMuleVersions()) {
            if (version.isDefaultVersion()) {
                return version;
            }
        }
        throw new UnexpectedException("No default mule version found");
    }

    public CHMuleVersion findCHMuleVersion(String muleVersion) throws NotFoundException, HttpException {
        for (CHMuleVersion version : findCHMuleVersions()) {
            if (version.getVersion().equalsIgnoreCase(muleVersion)) {
                return version;
            }
        }
        throw new NotFoundException("Unable to find mule version " + muleVersion);
    }

    public List<CHRegion> findAllCHRegions() throws HttpException {
        String json = client.getHttpHelper().httpGet("/cloudhub/api/regions", this);
        return client.getJsonHelper().readJsonList(CHRegion.class, json, this);
    }

    public CHRegion findDefaultCHRegion() throws HttpException {
        for (CHRegion region : findAllCHRegions()) {
            if (region.isDefaultRegion()) {
                return region;
            }
        }
        throw new UnexpectedException("No default mule version found");
    }

    public List<CHWorkerType> findAllWorkerTypes() throws HttpException {
        String json = client.getHttpHelper().httpGet("/cloudhub/api/organization", this);
        return client.getJsonHelper().readJsonList(CHWorkerType.class, json, this, "/plan/workerTypes");
    }

    public CHWorkerType findWorkerTypeByName(String name) throws HttpException, NotFoundException {
        for (CHWorkerType workerType : findAllWorkerTypes()) {
            if (workerType.getName().equalsIgnoreCase(name)) {
                return workerType;
            }
        }
        throw new NotFoundException("Unable to find worker type in plan: " + name);
    }

    public CHWorkerType findSmallestWorkerType() throws HttpException {
        CHWorkerType smallest = null;
        for (CHWorkerType workerType : findAllWorkerTypes()) {
            if (smallest == null || smallest.getWorkerVal().compareTo(workerType.getWorkerVal()) > 0) {
                smallest = workerType;
            }
        }
        return smallest;
    }
}
