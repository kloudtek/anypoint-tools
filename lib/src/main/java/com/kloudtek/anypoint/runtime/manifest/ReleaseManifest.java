package com.kloudtek.anypoint.runtime.manifest;

import java.util.ArrayList;
import java.util.List;

public class ReleaseManifest {
    private List<ReleaseManifestArtifact> artifacts = new ArrayList<>();

    public List<ReleaseManifestArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ReleaseManifestArtifact> artifacts) {
        this.artifacts = artifacts;
    }
}
