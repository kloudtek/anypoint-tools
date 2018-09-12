package com.kloudtek.anypoint.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;

import java.util.List;

public class HApplication extends AnypointObject<Server> {
    private Integer id;
    private String name;
    private String desiredStatus;
    private String lastReportedStatus;
    private boolean started;
    private ApplicationArtifact artifact;
    private List<ApplicationDeployment> applicationDeployments;

    public HApplication(Server parent) {
        super(parent);
    }

    public HApplication() {
    }

    public String getUriPath() {
        return "/hybrid/api/v1/applications/" + id;
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

    @JsonProperty("serverArtifacts")
    public List<ApplicationDeployment> getApplicationDeployments() {
        return applicationDeployments;
    }

    public void setApplicationDeployments(List<ApplicationDeployment> applicationDeployments) {
        this.applicationDeployments = applicationDeployments;
    }

    public boolean matchDigest(String digest) {
        return artifact != null && artifact.getFileChecksum().equals(digest);
    }

    public HApplication refresh() throws HttpException {
        String json = httpHelper.httpGet(getUriPath(), parent.getParent());
        return jsonHelper.readJson(new HApplication(parent), json, "/data");
    }
}
