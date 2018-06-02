package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;

import java.util.Map;

public class APIPortal extends AnypointObject<API> {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonIgnore
    private API apiVersion;

    public APIPortal() {
    }

    public APIPortal(API apiVersion) {
        super(apiVersion);
        this.apiVersion = apiVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public API getApiVersion() {
        return apiVersion;
    }

    public APIPortal updateName(String name ) throws HttpException {
        Map<String, Object> req = jsonHelper.buildJsonMap().set("name", name).set("id", id).toMap();
        String json = client.getHttpHelper().httpPatch(apiVersion.getUriPath() + "/portal", req);
        return jsonHelper.readJson(new APIPortal(parent),json);
    }
}
