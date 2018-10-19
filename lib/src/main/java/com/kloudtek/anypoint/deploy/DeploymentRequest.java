package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.ClientApplication;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.api.provision.APIProvisioningResult;
import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.unpack.FileType;
import com.kloudtek.unpack.Unpacker;
import com.kloudtek.unpack.transformer.SetPropertyTransformer;
import com.kloudtek.unpack.transformer.Transformer;
import com.kloudtek.util.TempFile;
import com.kloudtek.util.UnexpectedException;
import com.kloudtek.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class DeploymentRequest {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentRequest.class);
    protected Environment environment;
    protected String appName;
    protected File file;
    protected String filename;
    protected APIProvisioningConfig apiProvisioningConfig;
    protected Map<String, String> properties;

    public DeploymentRequest() {
    }

    public DeploymentRequest(Environment environment, String appName, File file, String filename,
                             Map<String, String> properties,
                             APIProvisioningConfig apiProvisioningConfig) {
        this.environment = environment;
        this.appName = appName;
        this.file = file;
        this.filename = filename;
        this.properties = properties;
        this.apiProvisioningConfig = apiProvisioningConfig;
    }

    public DeploymentResult deploy() throws ProvisioningException, IOException, HttpException {
        AnypointClient client = environment.getClient();
        boolean tmpFile = false;
        try {
            environment = environment.refresh();
            APIProvisioningResult provisioningResult = null;
            List<Transformer> transformers = new ArrayList<>();
            if (apiProvisioningConfig != null) {
                ZipFile zipFile = new ZipFile(file);
                ZipEntry anypointJson = zipFile.getEntry("anypoint.json");
                if (anypointJson != null) {
                    logger.debug("Found anypoint.json, provisioning");
                    APIProvisioningDescriptor apiProvisioningDescriptor;
                    try (InputStream is = zipFile.getInputStream(anypointJson)) {
                        apiProvisioningDescriptor = client.getJsonHelper().getJsonMapper().readValue(is, APIProvisioningDescriptor.class);
                    }
                    provisioningResult = apiProvisioningDescriptor.provision(environment, apiProvisioningConfig);
                    if (apiProvisioningConfig.isInjectApiId()) {
                        properties.put(apiProvisioningConfig.getInjectApiIdKey(), Integer.toString(provisioningResult.getApi().getId()));
                        properties.put("anypoint.platform.client_id",environment.getClientId());
                        properties.put("anypoint.platform.client_secret",environment.getClientSecret());
                    }
                    ClientApplication clientApp = provisioningResult.getClientApplication();
                    if (clientApp != null && apiProvisioningConfig.isInjectClientIdSecret()) {
                        properties.put(apiProvisioningConfig.getInjectClientIdSecretKey() + ".id", clientApp.getClientId());
                        properties.put(apiProvisioningConfig.getInjectClientIdSecretKey() + ".secret", clientApp.getClientSecret());
                    }
                }
            }
            preDeploy(provisioningResult, apiProvisioningConfig, transformers);
            if (!transformers.isEmpty()) {
                try {
                    File oldFile = file;
                    file = new TempFile("tranformed", filename);
                    Unpacker unpacker = new Unpacker(oldFile, FileType.ZIP, file, FileType.ZIP);
                    unpacker.addTransformers(transformers);
                    unpacker.unpack();
                } catch (Exception e) {
                    throw new ProvisioningException("An error occurred while applying application " + appName + " transformations: " + e.getMessage(), e);
                }
                tmpFile = true;
            }
            return doDeploy();
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        } finally {
            if (tmpFile) {
                IOUtils.close((TempFile) file);
            }
        }
    }

    protected abstract void preDeploy(APIProvisioningResult result, APIProvisioningConfig config, List<Transformer> transformers);

    protected abstract DeploymentResult doDeploy() throws IOException, HttpException;

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public APIProvisioningConfig getApiProvisioningConfig() {
        return apiProvisioningConfig;
    }

    public void setApiProvisioningConfig(APIProvisioningConfig apiProvisioningConfig) {
        this.apiProvisioningConfig = apiProvisioningConfig;
    }
}
