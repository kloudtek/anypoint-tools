package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;

import java.util.HashMap;

public class APIAccessContract extends AnypointObject<ClientApplication> {
    private Integer id;
    private String status;
    private APIVersion apiVersion;

    public APIAccessContract(ClientApplication parent) {
        super(parent);
    }

    public APIAccessContract() {
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty
    public APIVersion getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(APIVersion apiVersion) {
        this.apiVersion = apiVersion;
    }

    @JsonIgnore
    public boolean isApproved() {
        return status != null && status.equalsIgnoreCase("approved");
    }

    @JsonIgnore
    public boolean isRevoked() {
        return status != null && status.equalsIgnoreCase("revoked");
    }

    @JsonIgnore
    public boolean isPending() {
        return status != null && status.equalsIgnoreCase("pending");
    }

    public APIAccessContract restoreAccess() throws HttpException {
        String json = httpHelper.httpPost(apiVersion.getUriPath() + "/contracts/" + id + "/restore", new HashMap<String, Object>());
        return jsonHelper.readJson(new APIAccessContract(parent), json);
    }

    public APIAccessContract approveAccess() throws HttpException {
        String json = httpHelper.httpPost(apiVersion.getUriPath() + "/contracts/" + id + "/approve", new HashMap<String, Object>());
        return jsonHelper.readJson(new APIAccessContract(parent), json);
    }
}
