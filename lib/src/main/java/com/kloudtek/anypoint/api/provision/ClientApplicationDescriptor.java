package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientApplicationDescriptor {
    private String url;
    private String description;
    private String name = "${api.lname}-${organization.lname}-${environment.lname}";

    public ClientApplicationDescriptor() {
    }

    public ClientApplicationDescriptor(String url, String description, String name) {
        this.url = url;
        this.description = description;
        this.name = name;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
