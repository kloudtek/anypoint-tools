package com.kloudtek.anypointlib.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypointlib.AnypointObject;
import com.kloudtek.anypointlib.Environment;
import com.kloudtek.anypointlib.HttpException;

import java.io.IOException;

public class Server extends AnypointObject<Environment> {
    protected String id;
    protected String name;

    public Server() {
    }

    public Server(Environment environment) {
        super(environment);
    }

    public Server(Environment environment, String id) {
        super(environment);
        this.id = id;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void delete() throws HttpException {
        client.getHttpHelper().httpDelete("https://anypoint.mulesoft.com/hybrid/api/v1/servers/"+id,parent);
    }
}
