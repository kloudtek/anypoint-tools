package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ProvisionedAPIAccess {
    private String name;
    private String version;

    @JsonProperty(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(required = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvisionedAPIAccess)) return false;
        ProvisionedAPIAccess that = (ProvisionedAPIAccess) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }

    @Override
    public String toString() {
        return "ProvisionedAPIAccess{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
