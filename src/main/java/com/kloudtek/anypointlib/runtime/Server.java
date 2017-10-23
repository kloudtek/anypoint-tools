package com.kloudtek.anypointlib.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypointlib.AnypointObject;
import com.kloudtek.anypointlib.Environment;
import com.kloudtek.anypointlib.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        client.getHttpHelper().httpDelete("/hybrid/api/v1/servers/" + id, parent);
    }

    public Application deploy(String name, File file) throws IOException, HttpException {
        try (FileInputStream is = new FileInputStream(file)) {
            return deploy(name, file.getName(), is);
        }
    }

    public Application deploy(String name, String filename, InputStream stream) throws HttpException {
        HttpEntity entity = MultipartEntityBuilder.create().addTextBody("targetId", id)
                .addTextBody("artifactName", name)
                .addBinaryBody("file", stream, ContentType.APPLICATION_OCTET_STREAM, filename).build();
        String json = httpHelper.httpPost("/hybrid/api/v1/applications", entity, parent);
        return jsonHelper.readJson(new Application(this), json, "/data");
    }
}
