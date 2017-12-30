package com.kloudtek.anypoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.api.*;
import com.kloudtek.anypoint.util.JsonHelper;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Organization extends AnypointObject {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;

    public Organization() {
    }

    public Organization(AnypointClient client) {
        super(client);
    }

    public Organization(AnypointClient client, String id) {
        super(client);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Environment> getEnvironments() throws HttpException {
        return Environment.getEnvironments(client, this);
    }

    @NotNull
    public Environment findEnvironment(@NotNull String name) throws NotFoundException, HttpException {
        return Environment.getEnvironmentByName(name, client, this);
    }

    public API getAPI(@NotNull String name) throws HttpException, NotFoundException {
        for (API api : getAPIs(name)) {
            if (name.equals(api.getName())) {
                return api;
            }
        }
        throw new NotFoundException("API not found: " + name);
    }

    public APIList getAPIs() throws HttpException {
        return getAPIs(null);
    }

    public APIList getAPIs(String nameFilter) throws HttpException {
        return getAPIs(nameFilter, 0, 25);
    }

    public APIList getAPIs(String nameFilter, int offset, int limit) throws HttpException {
        URLBuilder urlBuilder = new URLBuilder("/apiplatform/repository/v2/organizations/" + id + "/apis").param("ascending", "false").param("limit", limit).param("offset", offset).param("sort", "createdDate");
        if (nameFilter != null) {
            urlBuilder.param("query", nameFilter);
        }
        String json = httpHelper.httpGet(urlBuilder.toString());
        JsonNode jsonTree = jsonHelper.readJsonTree(json);
        int total = jsonTree.get("total").intValue();
        JsonNode nodes = jsonTree.at("/apis");
        ArrayList<API> list = new ArrayList<>();
        for (JsonNode node : nodes) {
            list.add(jsonHelper.readJson(new API(this),node));
        }
        return new APIList(this, nameFilter, offset, limit, total, list);
    }

    public Environment createEnvironment(@NotNull String name, @NotNull Environment.Type type) throws HttpException {
        HashMap<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("type", type.name().toLowerCase());
        request.put("organizationId", id);
        String json = client.getHttpHelper().httpPost("https://anypoint.mulesoft.com/accounts/api/organizations/" + id + "/environments", request);
        return jsonHelper.readJson(new Environment(this),json);
    }

    public API createAPI(@NotNull String name, @NotNull String version) throws HttpException {
        return createAPI(name, version, null, null);
    }

    public API createAPI(@NotNull String name, @NotNull String version, @Nullable String endpoint) throws HttpException {
        return createAPI(name, version, endpoint, null);
    }

    public API createAPI(@NotNull String name, @NotNull String version, @Nullable String endpoint, @Nullable String description) throws HttpException {
        return API.create(this, name, version, endpoint, description);
    }

    public ClientApplication createClientApplication(String name, String url, String description) throws HttpException {
        return ClientApplication.create(this,name,url,description, Collections.emptyList(), null);
    }

    public ClientApplication createClientApplication(String name, String url, String description, List<String> redirectUri, String apiEndpoints) throws HttpException {
        return ClientApplication.create(this,name,url,description,redirectUri,apiEndpoints);
    }

    public Organization createSubOrganization(String name, String ownerId, boolean createSubOrgs, boolean createEnvironments) throws HttpException {
        return createSubOrganization(name, ownerId, createSubOrgs, createEnvironments, false,
                0, 0, 0, 0, 0, 0);
    }

    public ClientApplicationList listClientApplications() throws HttpException {
        return listClientApplications(null);
    }

    public ClientApplicationList listClientApplications(@Nullable String filter) throws HttpException {
        return ClientApplication.find(this, filter);
    }

    public ClientApplication findClientApplication(@NotNull String name) throws HttpException, NotFoundException {
        return findClientApplication(name, true);
    }

    public ClientApplication findClientApplication(@NotNull String name, boolean fullData) throws HttpException, NotFoundException {
        for (ClientApplication app : listClientApplications(name)) {
            if( name.equals(app.getName()) ) {
                if (fullData) {
                    return jsonHelper.readJson(app, httpHelper.httpGet(app.getUriPath()));
                } else {
                    return app;
                }
            }
        }
        throw new NotFoundException("Client application not found: " + name);
    }


    public Organization createSubOrganization(String name, String ownerId, boolean createSubOrgs, boolean createEnvironments,
                                           boolean globalDeployment, int vCoresProduction, int vCoresSandbox, int vCoresDesign,
                                           int staticIps, int vpcs, int loadBalancer) throws HttpException {
        JsonHelper.MapBuilder builder = client.getJsonHelper().buildJsonMap().set("name", name).set("parentOrganizationId", id).set("ownerId", ownerId);
        Map<String, Object> req = builder.addMap("entitlements").set("createSubOrgs", createSubOrgs).set("createEnvironments", createEnvironments)
                .set("globalDeployment", globalDeployment)
                .setNested("vCoresProduction", "assigned", vCoresProduction)
                .setNested("vCoresSandbox", "assigned", vCoresSandbox)
                .setNested("vCoresDesign", "assigned", vCoresDesign)
                .setNested("staticIps", "assigned", staticIps)
                .setNested("vpcs", "assigned", vpcs)
                .setNested("loadBalancer", "assigned", loadBalancer)
                .setNested("staticIps", "assigned", staticIps).toMap();
        String json = httpHelper.httpPost("/accounts/api/organizations", req);
        return jsonHelper.readJson(new Organization(client),json);
    }

    public void delete() throws HttpException {
        httpHelper.httpDelete("/accounts/api/organizations/" + id);
    }

    @JsonIgnore
    public String getUriPath() {
        return "/apiplatform/repository/v2/organizations/" + id;
    }

    public RequestAPIAccessResult requestAPIAccess(String clientApplicationName, String apiName, String apiVersionName, boolean autoApprove, boolean autoRestore, String slaTier) throws HttpException, RequestAPIAccessException, NotFoundException {
        ClientApplication clientApplication;
        try {
            clientApplication = findClientApplication(clientApplicationName);
        } catch (NotFoundException e) {
            clientApplication = createClientApplication(clientApplicationName, "", "");
        }
        return requestAPIAccess(clientApplication, apiName, apiVersionName, autoApprove, autoRestore, slaTier);
    }

    public RequestAPIAccessResult requestAPIAccess(ClientApplication clientApplication, String apiName, String apiVersionName, boolean autoApprove, boolean autoRestore, String slaTier) throws HttpException, RequestAPIAccessException, NotFoundException {
        APIVersion version = getAPI(apiName).getVersion(apiVersionName);
        APIAccessContract contract;
        try {
            contract = clientApplication.findContract(version);
        } catch (NotFoundException e) {
            if (StringUtils.isEmpty(slaTier)) {
                List<SLATier> tiers = version.getSLATiers();
                slaTier = tiers.size() == 1 ? tiers.get(0).getName() : null;
            }
            SLATier tier = slaTier != null ? version.getSLATier(slaTier) : null;
            contract = clientApplication.requestAPIAccess(version, tier);
        }
        if (contract.isPending()) {
            if (autoApprove) {
                contract = contract.approveAccess();
                if (contract.isApproved()) {
                    return RequestAPIAccessResult.GRANTED;
                } else {
                    throw new RequestAPIAccessException("Failed to auto-approve API access (status: " + contract.getStatus() + " )");
                }
            } else {
                return RequestAPIAccessResult.PENDING;
            }
        } else if (contract.isRevoked()) {
            if (autoRestore) {
                contract = contract.restoreAccess();
                if (contract.isApproved()) {
                    return RequestAPIAccessResult.RESTORED;
                } else {
                    throw new RequestAPIAccessException("Failed to restore access to client application");
                }
            } else {
                throw new RequestAPIAccessException("API access is currently revoked, cannot grant access");
            }
        } else if (contract.isApproved()) {
            return RequestAPIAccessResult.GRANTED;
        } else {
            throw new RequestAPIAccessException("Unknown contract status: " + contract.getStatus());
        }
    }

    public enum RequestAPIAccessResult {
        GRANTED, RESTORED, PENDING
    }
}
