package com.kloudtek.anypoint.api.policy;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class JsonPolicy extends Policy {
    private String data;

    @JsonRawValue
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
