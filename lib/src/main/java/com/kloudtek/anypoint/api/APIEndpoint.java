package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class APIEndpoint {
    private String type;
    private String uri;
    private boolean mule4;

    @JsonProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty("muleVersion4OrAbove")
    public boolean isMule4() {
        return mule4;
    }

    public void setMule4(boolean mule4) {
        this.mule4 = mule4;
    }
}
