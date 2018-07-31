package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LockResult {
    @JsonProperty
    private boolean locked;
    @JsonProperty
    private String name;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
