package com.kloudtek.anypoint.runtime.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.Organization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;

public abstract class ReleaseManifestDAO {
    protected Organization organization;
    protected String orgId;
    protected String groupId;
    protected String artifactId;
    protected String version;
    protected String name;

    protected ReleaseManifestDAO(@NotNull Organization organization) {
        this.organization = organization;
    }

    public static ReleaseManifestDAO load(@NotNull Organization organization, @NotNull URI uri) {
        String type = uri.getScheme();
        String params = uri.getSchemeSpecificPart();
        switch (type.toLowerCase()) {
            case "exchange":
                return new ReleaseManifestExchangeDAO(organization, params);
            default:
                throw new IllegalArgumentException("Invalid uri scheme: " + uri);
        }
    }

    public synchronized void setArtifactVersion(@Nullable String groupId, @NotNull String artifactId, @NotNull String version) {
        ReleaseManifestArtifact artifact = findArtifact(groupId, artifactId);
        if (artifact == null) {
            artifacts.add(new ReleaseManifestArtifact(orgId, groupId, artifactId, version));
        } else {
            artifact.setVersion(version);
        }
    }

    public synchronized void removeArtifact(String groupId, String artifactId) {
        ReleaseManifestArtifact artifact = findArtifact(groupId, artifactId);
        if (artifact != null) {
            artifacts.remove(artifact);
        }
    }

    public ReleaseManifestArtifact findArtifact(String groupId, String artifactId) {
        for (ReleaseManifestArtifact artifact : artifacts) {
            if (artifact.getGroupId().equals(groupId) && artifact.getArtifactId().equals(artifactId)) {
                return artifact;
            }
        }
        return null;
    }

    @JsonProperty
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
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

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public List<ReleaseManifestArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ReleaseManifestArtifact> artifacts) {
        this.artifacts = artifacts;
    }
}
