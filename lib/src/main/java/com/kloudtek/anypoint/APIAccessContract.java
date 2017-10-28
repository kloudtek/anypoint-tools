package com.kloudtek.anypoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.api.ClientApplication;

public class APIAccessContract extends AnypointObject<ClientApplication> {
    private Integer id;
    private String status;

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
}
