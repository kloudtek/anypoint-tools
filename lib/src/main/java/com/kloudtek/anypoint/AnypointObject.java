package com.kloudtek.anypoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AnypointObject<X extends AnypointObject> implements Serializable {
    @JsonIgnore
    protected String json;
    @JsonIgnore
    protected AnypointClient client;
    @JsonIgnore
    protected HttpHelper httpHelper;
    @JsonIgnore
    protected JsonHelper jsonHelper;
    @JsonIgnore
    protected X parent;

    public AnypointObject(AnypointClient client) {
        setClient(client);
    }

    public AnypointObject(X parent) {
        setParent(parent);
    }

    public AnypointObject() {
    }

    @JsonIgnore
    public AnypointClient getClient() {
        return client;
    }

    public void setParent(X parent) {
        this.parent = parent;
        setClient(parent.getClient());
    }

    public void setClient(AnypointClient client) {
        setClient(client,false);
    }

    public void setClient(AnypointClient client, boolean setParent) {
        this.client = client;
        httpHelper = client.getHttpHelper();
        jsonHelper = client.getJsonHelper();
        if( setParent && parent != null ) {
            parent.setClient(client);
        }
    }

    @JsonIgnore
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public X getParent() {
        return parent;
    }
}
