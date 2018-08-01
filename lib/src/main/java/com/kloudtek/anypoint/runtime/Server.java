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

    public DeploymentResult deploy(@NotNull String name, @NotNull File file, APIProvisioningConfig apiProvisioningConfig) throws IOException, HttpException, ProvisioningException {
        return deploy(name,file,file.getName(),apiProvisioningConfig);
    }

    /**
     * Deploy application
     * @param name Application name
     * @param file Application archive file
     * @param filename Application archive filename
     * @param apiProvisioningConfig API Provisioning config (if null no API provisioning will be done)
     * @return Deployment result
     * @throws IOException If an error occurs reading the application file
     * @throws HttpException If an error occurs commnunicating with anypoint
     */
    public DeploymentResult deploy(@NotNull String name, @NotNull File file, @NotNull String filename, APIProvisioningConfig apiProvisioningConfig) throws IOException, HttpException, ProvisioningException {
        boolean tmpFile = false;
        try {
            if( apiProvisioningConfig != null ) {
                ZipFile zipFile = new ZipFile(file);
                ZipEntry anypointJson = zipFile.getEntry("anypoint.json");
                if( anypointJson != null ) {
                    logger.debug("Found anypoint.json, provisioning");
                    APIProvisioningDescriptor apiProvisioningDescriptor;
                    try( InputStream is = zipFile.getInputStream(anypointJson) ) {
                        apiProvisioningDescriptor = client.getJsonHelper().getJsonMapper().readValue(is, APIProvisioningDescriptor.class);
                    }
                    APIProvisioningResult provisioningResult = apiProvisioningDescriptor.provision(parent, apiProvisioningConfig);
                    List<Transformer> transformers = new ArrayList<>();
                    if(apiProvisioningConfig.isInjectApiId()) {
                        transformers.add(new SetPropertyTransformer(apiProvisioningConfig.getInjectApiIdFile(),
                                apiProvisioningConfig.getInjectApiIdKey(),Integer.toString(provisioningResult.getApi().getId())));
                    }
                    ClientApplication clientApp = provisioningResult.getClientApplication();
                    if( clientApp != null && apiProvisioningConfig.isInjectClientIdSecret() ) {
                        HashMap<String,String> clientCreds = new HashMap<>();
                        clientCreds.put(apiProvisioningConfig.getInjectClientIdSecretKey()+".id", clientApp.getClientId());
                        clientCreds.put(apiProvisioningConfig.getInjectClientIdSecretKey()+".secret", clientApp.getClientSecret());
                        transformers.add(new SetPropertyTransformer(apiProvisioningConfig.getInjectClientIdSecretFile(), clientCreds);
                    }
                    if (!transformers.isEmpty()) {
                        try {
                            File oldFile = file;
                            file = new TempFile("tranformed", filename);
                            Unpacker unpacker = new Unpacker(oldFile, FileType.ZIP, file, FileType.ZIP);
                            unpacker.addTransformers(transformers);
                            unpacker.unpack();
                        } catch (Exception e) {
                            throw new ProvisioningException("An error occurred while applying application " + name + " transformations: " + e.getMessage(), e);
                        }
                        tmpFile = true;
                    }
                }
            }
            final File f = file;
            return deploy(name, new StreamSource() {
                @Override
                public String getFileName() {
                    return filename;
                }

                @Override
                public InputStream createInputStream() throws IOException {
                    return new FileInputStream(f);
                }
            });
        } finally {
            if( tmpFile ) {
                ((TempFile) file).close();
            }
        }
    }

    public DeploymentResult deploy(@NotNull String name, @NotNull StreamSource stream) throws HttpException, IOException {
        HttpHelper.MultiPartRequest request;
        long start = System.currentTimeMillis();
        try {
            logger.debug("Searching for pre-existing application named " + name);
            Application application = findApplication(name);
            logger.debug("Found application named {} : {}", name, application.getId());
            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(), parent);
        } catch (NotFoundException e) {
            logger.debug("Couldn't find application named {}", name);
            request = httpHelper.createMultiPartPostRequest("/hybrid/api/v1/applications", parent);
        }
        json = request.addText("targetId", id).addText("artifactName", name).addBinary("file", stream).execute();
        if (logger.isDebugEnabled()) {
            logger.debug("File upload took " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds");
        }
        Application application = jsonHelper.readJson(new Application(this), json, "/data");
        return new DeploymentResult(application);
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
