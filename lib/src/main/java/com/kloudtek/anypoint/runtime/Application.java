package com.kloudtek.anypoint.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.util.ThreadUtils;

import java.util.List;

public class Application extends AnypointObject<Server> {
    static final String DEPLOYMENT_FAILED = "DEPLOYMENT_FAILED";
    static final String STARTED = "STARTED";
    private Integer id;
    private String name;
    private String desiredStatus;
    private String lastReportedStatus;
    private boolean started;
    private ApplicationArtifact artifact;
    private List<ApplicationDeployment> applicationDeployments;

    public Application(Server parent) {
        super(parent);
    }

    public Application() {
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

    public void waitDeployed() throws HttpException, ApplicationDeploymentFailedException {
        waitDeployed(60000L, 1500L);
    }

    public void waitDeployed(long timeout, long retryDelay) throws HttpException, ApplicationDeploymentFailedException {
        long expires = System.currentTimeMillis() + timeout;
        for (; ; ) {
            String json = httpHelper.httpGet(getUriPath(), parent.getParent());
            Application application = jsonHelper.readJson(new Application(parent), json, "/data");
            if (application.isStarted()) {
                return;
            } else if (DEPLOYMENT_FAILED.equals(application.getLastReportedStatus())) {
                throw ApplicationDeploymentFailedException.create(application);
            } else {
                if (expires > System.currentTimeMillis()) {
                    ThreadUtils.sleep(retryDelay);
                } else {
                    throw ApplicationDeploymentFailedException.create(application);
                }
            }
        }
    }
}
