package com.kloudtek.anypoint.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpHelperOperation {
    private String method;
    private String path;
    private String content;
    private String result;

    public HttpHelperOperation() {
    }

    public HttpHelperOperation(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @JsonProperty
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
