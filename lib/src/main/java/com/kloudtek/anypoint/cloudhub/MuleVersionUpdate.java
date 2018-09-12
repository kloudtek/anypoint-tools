package com.kloudtek.anypoint.cloudhub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MuleVersionUpdate {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;

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
}
