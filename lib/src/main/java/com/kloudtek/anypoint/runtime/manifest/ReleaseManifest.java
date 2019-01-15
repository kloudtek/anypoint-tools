package com.kloudtek.anypoint.runtime.manifest;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReleaseManifest {
    private String version;
    private List<ReleaseManifestArtifact> artifacts = new ArrayList<>();

    public ReleaseManifest() {
    }

    public ReleaseManifest(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @NotNull
    public List<ReleaseManifestArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(@NotNull List<ReleaseManifestArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    public ReleaseManifestArtifact findArtifact(@NotNull String groupId, @NotNull String artifactId) {
        for (ReleaseManifestArtifact artifact : artifacts) {
            if (groupId.equalsIgnoreCase(artifact.getGroupId()) && artifactId.equalsIgnoreCase(artifact.getArtifactId())) {
                return artifact;
            }
        }
        return null;
    }

    public void update(String groupId, String artifactId, String version) {
        ReleaseManifestArtifact artifact = findArtifact(groupId, artifactId);
        if (artifact == null) {
            artifact = new ReleaseManifestArtifact(groupId, artifactId, version);
            artifacts.add(artifact);
        } else {
            artifact.setVersion(version);
        }
    }
}
