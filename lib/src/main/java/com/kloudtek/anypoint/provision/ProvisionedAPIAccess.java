package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProvisionedAPIAccess {
    private String name;
    private String version;

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
