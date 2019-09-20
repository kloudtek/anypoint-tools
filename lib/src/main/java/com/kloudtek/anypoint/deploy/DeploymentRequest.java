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
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.unpack.FileType;
import com.kloudtek.unpack.Unpacker;
import com.kloudtek.unpack.transformer.Transformer;
import com.kloudtek.util.TempFile;
import com.kloudtek.util.UnexpectedException;
import com.kloudtek.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class DeploymentRequest {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentRequest.class);
    protected Environment environment;
    protected String appName;
    protected ApplicationSource source;
    protected String filename;
    protected APIProvisioningConfig apiProvisioningConfig;
    protected Map<String, String> properties;

    public DeploymentRequest() {
    }

    public DeploymentRequest(Environment environment, String appName, ApplicationSource source, String filename,
                             Map<String, String> properties,
                             APIProvisioningConfig apiProvisioningConfig) {
        this.environment = environment;
        this.appName = appName;
        this.source = source;
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
                APIProvisioningDescriptor apiProvisioningDescriptor = source.getAPIProvisioningDescriptor();
                if (apiProvisioningDescriptor != null) {
                    logger.debug("Found anypoint.json, provisioning");
                    provisioningResult = apiProvisioningDescriptor.provision(environment, apiProvisioningConfig);
                    if (apiProvisioningConfig.isInjectApiId()) {
                        initProperties();
                        properties.put(apiProvisioningConfig.getInjectApiIdKey(), provisioningResult.getApi().getId());
                        properties.put("anypoint.platform.client_id", environment.getClientId());
                        properties.put("anypoint.platform.client_secret", environment.getClientSecret());
                    }
                    ClientApplication clientApp = provisioningResult.getClientApplication();
                    if (clientApp != null && apiProvisioningConfig.isInjectClientIdSecret()) {
                        initProperties();
                        properties.put(apiProvisioningConfig.getInjectClientIdSecretKey() + ".id", clientApp.getClientId());
                        properties.put(apiProvisioningConfig.getInjectClientIdSecretKey() + ".secret", clientApp.getClientSecret());
                    }
                } else {
                    logger.debug("no anypoint.json found, skipping provisioning");
                }
            }
            preDeploy(provisioningResult, apiProvisioningConfig, transformers);
            if (!transformers.isEmpty()) {
                try {
                    if (source instanceof FileApplicationSource || source.getLocalFile() != null) {
                        File oldFile = source.getLocalFile();
                        File newFile = new TempFile("tranformed", filename);
                        source = new FileApplicationSource(client, newFile);
                        Unpacker unpacker = new Unpacker(oldFile, FileType.ZIP, newFile, FileType.ZIP);
                        unpacker.addTransformers(transformers);
                        unpacker.unpack();
                    } else if (source instanceof ExchangeApplicationSource) {
                        throw new ProvisioningException("Transformations on exchange sources not supported at this (so OnPrem provisioned deployments won't work with exchange sources until this feature is added)");
                    }
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
                IOUtils.close((TempFile) source.getLocalFile());
            }
        }
    }

    private void initProperties() {
        if (properties == null) {
            properties = new HashMap<>();
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

    public ApplicationSource getSource() {
        return source;
    }

    public void setSource(ApplicationSource source) {
        this.source = source;
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

    protected String executeRequest(long start, HttpHelper.MultiPartRequest multiPartRequest) throws HttpException, IOException {
        String json = multiPartRequest.execute();
        if (logger.isDebugEnabled()) {
            logger.debug("File upload took " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds");
        }
        return json;
    }
}
