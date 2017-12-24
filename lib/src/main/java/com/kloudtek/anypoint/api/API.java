package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API extends AnypointObject<Organization> {
    private static final Logger logger = Logger.getLogger(API.class.getName());
    private String id;
    private String name;
    private List<APIVersion> versions;

    public API() {
    }

    public API(Organization organization) {
        super(organization);
    }

    public API(Organization organization, String id, String name) {
        super(organization);
        this.id = id;
        this.name = name;
    }

    @JsonIgnore
    public String getUriPath() {
        return "/apiplatform/repository/v2/organizations/" + parent.getId() + "/apis/" + id;
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
    public List<APIVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<APIVersion> versions) {
        this.versions = versions;
        if (versions != null && client != null) {
            for (APIVersion version : versions) {
                version.setParent(this);
            }
        }
    }

    @JsonIgnore
    public APIVersion getVersion(String version) throws NotFoundException, HttpException {
        return getVersion(version,false);
    }

    @JsonIgnore
    public APIVersion getVersion(String version, boolean fullData) throws NotFoundException, HttpException {
        if( fullData ) {
            String json = httpHelper.httpGet(getUriPath() + "/versions/" + getVersionImpl(version).getId());
            return jsonHelper.readJson(new APIVersion(this), json );
        } else {
            return getVersionImpl(version);
        }
    }

    private APIVersion getVersionImpl(String version) throws NotFoundException {
        for (APIVersion v : versions) {
            if (version.equals(v.getName())) {
                return v;
            }
        }
        throw new NotFoundException("Unable to find version " + version);
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public List<APIPortal> getPortals() throws HttpException {
        String json = httpHelper.httpGet(getUriPath() + "/portals");
        ArrayList<APIPortal> apiPortals = new ArrayList<>();
        for (JsonNode node : jsonHelper.readJsonTree(json)) {
            apiPortals.add(jsonHelper.readJson(new APIPortal(), node ));
        }
        return apiPortals;
    }

    @Override
    public void setClient(AnypointClient client) {
        super.setClient(client);
        if( versions != null ) {
            for (APIVersion version : versions) {
                version.setClient(client);
            }
        }
    }

    public static API create(Organization organization, @NotNull String name, @NotNull String version, @Nullable String endpoint, @Nullable String description) throws HttpException {
        HashMap<String, Object> req = new HashMap<>();
        req.put("name", name);
        HashMap<String, Object> versionReq = new HashMap<>();
        versionReq.put("name", version);
        versionReq.put("endpointUri", endpoint != null ? endpoint : "");
        versionReq.put("description", description != null ? description : "");
        req.put("version", versionReq);
        String json = organization.getClient().getHttpHelper().httpPost("https://anypoint.mulesoft.com/apiplatform/repository/v2/organizations/" + organization.getId() + "/apis", req);
        API api = null;
        try {
            api = organization.getClient().getJsonHelper().readJson(new API(organization), json);
            api.getVersion(version).updateEndpoint(endpoint);
        } catch (Exception e) {
            try {
                if (api != null) {
                    api.delete();
                }
            } catch (HttpException e2) {
                logger.log(Level.WARNING, "Unable to rollback API Creation",e);
                throw new HttpException("Error while setting API endpoint but unable to delete created API",e);
            }
        }
        return api;
    }

    private void delete() throws HttpException {
        httpHelper.httpDelete(getUriPath());
    }

    public APIVersion createVersion(String version) throws HttpException {
        return createVersion(version, null, null);
    }

    public APIVersion createVersion(String version, String endpointUri, String description) throws HttpException {
        Map<String, Object> req = jsonHelper.buildJsonMap().set("name", version).set("endpointUri", endpointUri)
                .set("description", description).toMap();
        String json = httpHelper.httpPost(getUriPath() + "/versions", req);
        return jsonHelper.readJson(new APIVersion(this), json);
    }
}
