package com.kloudtek.anypoint.cfg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private Map<String, ConfigProfile> profiles;
    private File file;
    private String defaultProfileName = "default";

    public Config(File file) {
        this.file = file;
    }

    public Config(File file, ConfigProfile profile) {
        this.file = file;
        profiles = new HashMap<>();
        profiles.put("default", profile);
    }

    @JsonProperty("profiles")
    public Map<String, ConfigProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, ConfigProfile> profiles) {
        this.profiles = profiles;
    }

    @JsonProperty("defaultProfileName")
    public String getDefaultProfileName() {
        return defaultProfileName;
    }

    public void setDefaultProfileName(String defaultProfileName) {
        this.defaultProfileName = defaultProfileName;
    }

    @JsonIgnore
    public ConfigProfile getDefaultProfile() {
        return getProfile(defaultProfileName);
    }

    public void save() throws IOException {
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    @JsonIgnore
    public String getFilePath() {
        return file.getPath();
    }

    public synchronized ConfigProfile getProfile(String profileName) {
        ConfigProfile configProfile = profiles.get(profileName);
        if (configProfile == null) {
            configProfile = new ConfigProfile();
            profiles.put(profileName, configProfile);
        }
        return configProfile;
    }
}
