package com.kloudtek.anypointlib.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypointlib.AnypointObject;

public class Application extends AnypointObject<Server> {
    private Integer id;
    private String name;
    private String desiredStatus;
    private String lastReportedStatus;
    private boolean started;
    private ApplicationArtifact artifact;

    public Application(Server parent) {
        super(parent);
    }

    public Application() {
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
    public String getDesiredStatus() {
        return desiredStatus;
    }

    public void setDesiredStatus(String desiredStatus) {
        this.desiredStatus = desiredStatus;
    }

    @JsonProperty
    public String getLastReportedStatus() {
        return lastReportedStatus;
    }

    public void setLastReportedStatus(String lastReportedStatus) {
        this.lastReportedStatus = lastReportedStatus;
    }

    @JsonProperty
    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    @JsonProperty
    public ApplicationArtifact getArtifact() {
        return artifact;
    }

    public void setArtifact(ApplicationArtifact artifact) {
        this.artifact = artifact;
    }
}
