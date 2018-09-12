package com.kloudtek.anypoint.cloudhub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CHMuleVersion {
    @JsonProperty
    private String version;
    @JsonProperty
    private String recommended;
    @JsonProperty
    private long endOfSupportDate;
    @JsonProperty
    private long endOfLifeDate;
    @JsonProperty
    private MuleVersionUpdate latestUpdate;
    @JsonProperty(value = "default")
    private boolean defaultVersion;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRecommended() {
        return recommended;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public long getEndOfSupportDate() {
        return endOfSupportDate;
    }

    public void setEndOfSupportDate(long endOfSupportDate) {
        this.endOfSupportDate = endOfSupportDate;
    }

    public long getEndOfLifeDate() {
        return endOfLifeDate;
    }

    public void setEndOfLifeDate(long endOfLifeDate) {
        this.endOfLifeDate = endOfLifeDate;
    }

    public MuleVersionUpdate getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(MuleVersionUpdate latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public boolean isDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(boolean defaultVersion) {
        this.defaultVersion = defaultVersion;
    }
}
