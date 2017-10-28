package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.*;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ClientApplication extends AnypointObject<Organization> {
    private Integer id;
    private String name;
    private String description;
    private String url;
    private String clientId;
    private String clientSecret;

    public ClientApplication(AnypointClient client) {
        super(client);
    }

    public ClientApplication(Organization parent) {
        super(parent);
    }

    public ClientApplication() {
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @JsonIgnore
    public String getUriPath() {
        return parent.getUriPath()+"/applications/"+id;
    }

    public static ClientApplication create(@NotNull Organization organization, @NotNull String name, String url, String description, List<String> redirectUri, String apiEndpoints) throws HttpException {
        AnypointClient client = organization.getClient();
        Map<String, Object> req = client.getJsonHelper().buildJsonMap().set("name", name.trim()).set("url", url)
                .set("description", description != null ? description : "")
                .set("redirectUri",redirectUri).set("apiEndpoints",apiEndpoints)
                .toMap();
        String json = client.getHttpHelper().httpPost(organization.getUriPath() + "/applications", req);
        return client.getJsonHelper().readJson(new ClientApplication(organization), json );
    }

    public static List<ClientApplication> find(Organization organization, String filter) throws HttpException {
        URLBuilder url = new URLBuilder(organization.getUriPath() + "/applications");
        if( filter != null ) {
            url.param("query",filter);
        }
        String json = organization.getClient().getHttpHelper().httpGet(url.toString());
        return organization.getClient().getJsonHelper().readJsonList(ClientApplication.class, json, organization, "/applications");
    }

    public void delete() throws HttpException {
        httpHelper.httpDelete(getUriPath());
    }

    public APIAccessContract requestAPIAccess(APIVersion apiVersion) throws HttpException {
        return requestAPIAccess(apiVersion, null,null,false);
    }

    public APIAccessContract requestAPIAccess(APIVersion apiVersion, String partyId, String partyName, boolean acceptedTerms) throws HttpException {
        Map<String, Object> req = jsonHelper.buildJsonMap()
                .set("apiVersionId",apiVersion.getId()).set("applicationId",id)
                .set("partyId",partyId) .set("partyName",partyName) .set("acceptedTerms",acceptedTerms)
                .toMap();
        String json = httpHelper.httpPost(parent.getUriPath() + "/applications/" + id + "/contracts",req);
        return jsonHelper.readJson(new APIAccessContract(this), json );
    }
}
