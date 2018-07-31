package com.kloudtek.anypoint.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class HttpHelperRecording {
    @JsonProperty
    private String orgName;
    @JsonProperty
    private List<HttpHelperOperation> operations = new ArrayList<>();

    public void addOperation(HttpHelperOperation operation) {
        operations.add(operation);
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<HttpHelperOperation> getOperations() {
        return operations;
    }
}
