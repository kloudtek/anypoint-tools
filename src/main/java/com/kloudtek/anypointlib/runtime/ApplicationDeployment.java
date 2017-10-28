package com.kloudtek.anypointlib.runtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDeployment {
    private Integer id;
    private String lastReportedStatus;
    private String message;

    public ApplicationDeployment() {
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty
    public String getLastReportedStatus() {
        return lastReportedStatus;
    }

    public void setLastReportedStatus(String lastReportedStatus) {
        this.lastReportedStatus = lastReportedStatus;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
