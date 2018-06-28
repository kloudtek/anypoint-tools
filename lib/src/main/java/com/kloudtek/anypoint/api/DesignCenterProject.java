package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.Organization;

import java.util.Collections;
import java.util.Map;

public class DesignCenterProject extends AnypointObject<Organization> {
    private String id;
    private String name;
    private String type;
    private String organizationId;

    public DesignCenterProject() {
    }

    public DesignCenterProject(Organization organization) {
        super(organization);
    }

    public static DesignCenterProject create(Organization organization, String name, String type, boolean visualDesignerMode, String ownerId) throws HttpException {
        AnypointClient client = organization.getClient();
        Map<String, Object> req = client.getJsonHelper().buildJsonMap()
                .set("name", name)
                .set("type", type)
                .set("visualDesignerMode", visualDesignerMode)
                .set("environmentId", organization.getId())
                .set("classifier", type).toMap();
        String json = client.getHttpHelper().httpPostWithOrgAndOwner("/designcenter/api-designer/projects", req, organization.getId(), client.getUserId() );
        return client.getJsonHelper().readJson(new DesignCenterProject(organization), json);
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public DesignCenterProjectExchange getExchange(String branch) throws HttpException {
        String json = client.getHttpHelper().httpGetWithOrgAndOwner("/designcenter/api-designer/projects/" + id + "/branches/" + branch + "/exchange", organizationId, client.getUserId());
        return client.getJsonHelper().readJson(new DesignCenterProjectExchange(this, branch),json);
    }

    public LockResult lock(String branch) throws HttpException {
        String json = httpHelper.httpPostWithOrgAndOwner("/designcenter/api-designer/projects/" + id + "/branches/" + branch + "/acquireLock",
                null, organizationId, client.getUserId());
        return client.getJsonHelper().readJson(new LockResult(), json);
    }

    public LockResult unlock(String branch) throws HttpException {
        String json = httpHelper.httpPostWithOrgAndOwner("/designcenter/api-designer/projects/" + id + "/branches/" + branch + "/releaseLock",
                null, organizationId, client.getUserId());
        return client.getJsonHelper().readJson(new LockResult(), json);
    }

    public void publishExchange(String branch, String assetId, String name, String mainFile, String assetVersion, String apiVersion) throws HttpException {
        Map<String, Object> req = jsonHelper.buildJsonMap()
                .set("name",name)
                .set("apiVersion",apiVersion)
                .set("version",assetVersion)
                .set("main",mainFile)
                .set("assetId",assetId)
                .set("groupId",organizationId)
                .set("classifier",type)
                .set("isVisual",false)
                .set("tags", Collections.emptyList())
                .toMap();
        lock(branch);
        try {
            String json = httpHelper.httpPostWithOrgAndOwner("/designcenter/api-designer/projects/" + id + "/branches/" + branch + "/publish/exchange",
                    req, organizationId, client.getUserId());
        } finally {
            unlock(branch);
        }
    }
}
