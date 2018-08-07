package com.kloudtek.anypoint.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.ClientApplication;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.api.provision.APIProvisioningResult;
import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.StreamSource;
import com.kloudtek.kryptotek.DigestAlgorithm;
import com.kloudtek.kryptotek.DigestUtils;
import com.kloudtek.unpack.FileType;
import com.kloudtek.unpack.Unpacker;
import com.kloudtek.unpack.transformer.SetPropertyTransformer;
import com.kloudtek.unpack.transformer.Transformer;
import com.kloudtek.util.Hex;
import com.kloudtek.util.TempFile;
import com.kloudtek.util.UserDisplayableException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Server extends AnypointObject<Environment> {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
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

    public DeploymentResult deployOnPrem(@NotNull String name, @NotNull File file, APIProvisioningConfig apiProvisioningConfig) throws IOException, HttpException, ProvisioningException {
        return client.getDeploymentService().deployOnPrem(this,name,file,file.getName(),apiProvisioningConfig);
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
