package com.kloudtek.anypoint.cfg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigProfile {
    private String username;
    private String password;
    private String defaultOrganization;
    private String defaultEnvironment;

    @JsonProperty
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    public String getDefaultOrganization() {
        return defaultOrganization;
    }

    public void setDefaultOrganization(String defaultOrganization) {
        this.defaultOrganization = defaultOrganization;
    }

    @JsonProperty
    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }
}
