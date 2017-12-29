package com.kloudtek.anypoint.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.util.FileStreamSource;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.StreamSource;
import com.kloudtek.kryptotek.DigestAlgorithm;
import com.kloudtek.kryptotek.DigestUtils;
import com.kloudtek.util.Hex;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public boolean checkApplicationExist(@NotNull String name, @NotNull File file, boolean matchDigest) throws HttpException, IOException {
        try {
            Application application = findApplication(name);
            if (application != null) {
                return !matchDigest || application.matchDigest(new String(Hex.encodeHex(DigestUtils.digest(file, DigestAlgorithm.SHA1))));
            } else {
                return true;
            }
        } catch (NotFoundException e) {
            return false;
        }
    }

    public Application deploy(@NotNull String name, @NotNull File file) throws IOException, HttpException {
        return deploy(name, new FileStreamSource(file));
    }

    public Application
    deploy(@NotNull String name, @NotNull StreamSource stream) throws HttpException, IOException {
        HttpHelper.MultiPartRequest request;
        try {
            Application application = findApplication(name);
            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(), parent);
        } catch (NotFoundException e) {
            request = httpHelper.createMultiPartPostRequest("/hybrid/api/v1/applications", parent);
        }
        json = request.addText("targetId", id).addText("artifactName", name).addBinary("file", stream).execute();
        return jsonHelper.readJson(new Application(this), json, "/data");
    }

    public List<Application> listApplication() throws HttpException {
        String json = httpHelper.httpGet("/hybrid/api/v1/applications?targetId=" + id, parent);
        return jsonHelper.readJsonList(Application.class, json, this, "/data");
    }

    public Application findApplication(String name) throws NotFoundException, HttpException {
        for (Application application : listApplication()) {
            if (name.equals(application.getName())) {
                return application;
            }
        }
        throw new NotFoundException("Unable to find application " + name + " in server " + parent.getId());
    }
}
