package com.kloudtek.anypoint.runtime.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseManifestArtifact {
    private String groupId;
    private String artifactId;
    private String version;

    public ReleaseManifestArtifact() {
    }

    public ReleaseManifestArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @JsonProperty
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty
    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
