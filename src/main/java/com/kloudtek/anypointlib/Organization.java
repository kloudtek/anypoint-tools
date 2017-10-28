package com.kloudtek.anypointlib;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypointlib.api.API;
import com.kloudtek.anypointlib.api.APIList;
import com.kloudtek.anypointlib.api.ClientApplication;
import com.kloudtek.anypointlib.util.JsonHelper;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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
    public Environment findEnvironment(@NotNull String name) throws IOException, NotFoundException, HttpException {
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

    public List<ClientApplication> listClientApplications() throws HttpException {
        return listClientApplications(null);
    }

    public List<ClientApplication> listClientApplications(@Nullable String filter) throws HttpException {
        return ClientApplication.find(this, filter);
    }

    public ClientApplication findClientApplication(@NotNull String name) throws HttpException, NotFoundException {
        for (ClientApplication app : listClientApplications(name)) {
            if( name.equals(app.getName()) ) {
                return app;
            }
        }
        throw new NotFoundException();
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
}
