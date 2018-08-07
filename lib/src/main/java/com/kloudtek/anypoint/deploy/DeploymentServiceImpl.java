package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AbstractService;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.api.ClientApplication;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.api.provision.APIProvisioningResult;
import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.unpack.FileType;
import com.kloudtek.unpack.Unpacker;
import com.kloudtek.unpack.transformer.SetPropertyTransformer;
import com.kloudtek.unpack.transformer.Transformer;
import com.kloudtek.util.TempFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("unused")
public class DeploymentServiceImpl extends AbstractService implements DeploymentService {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentServiceImpl.class);

    /**
     * Deploy application with optional automatic api provisioning
     *
     * @param target                target to deploy to
     * @param appName               Application name
     * @param file                  Application archive file
     * @param filename              Application archive filename
     * @param apiProvisioningConfig API Provisioning config (if null no API provisioning will be done)
     * @return Deployment result
     * @throws IOException   If an error occurs reading the application file
     * @throws HttpException If an error occurs commnunicating with anypoint
     */
    @Override
    public DeploymentResult deployOnPrem(Server target, @NotNull String appName, @NotNull File file, @NotNull String filename,
                                         APIProvisioningConfig apiProvisioningConfig) throws IOException, HttpException, ProvisioningException {
        OnPremDeploymentOperation deploymentRequest = new OnPremDeploymentOperation(target);
        return deploy(target.getParent(), appName, file, filename, apiProvisioningConfig, deploymentRequest);
    }

    private DeploymentResult deploy(Environment environment, @NotNull String appName, @NotNull File file, @NotNull String filename, APIProvisioningConfig apiProvisioningConfig, OnPremDeploymentOperation deploymentRequest) throws IOException, ProvisioningException, HttpException {
        boolean tmpFile = false;
        try {
            if (apiProvisioningConfig != null) {
                ZipFile zipFile = new ZipFile(file);
                ZipEntry anypointJson = zipFile.getEntry("anypoint.json");
                if (anypointJson != null) {
                    logger.debug("Found anypoint.json, provisioning");
                    APIProvisioningDescriptor apiProvisioningDescriptor;
                    try (InputStream is = zipFile.getInputStream(anypointJson)) {
                        apiProvisioningDescriptor = client.getJsonHelper().getJsonMapper().readValue(is, APIProvisioningDescriptor.class);
                    }
                    APIProvisioningResult provisioningResult = apiProvisioningDescriptor.provision(environment, apiProvisioningConfig);
                    List<Transformer> transformers = new ArrayList<>();
                    if (apiProvisioningConfig.isInjectApiId()) {
                        transformers.add(new SetPropertyTransformer(apiProvisioningConfig.getInjectApiIdFile(),
                                apiProvisioningConfig.getInjectApiIdKey(), Integer.toString(provisioningResult.getApi().getId())));
                    }
                    ClientApplication clientApp = provisioningResult.getClientApplication();
                    if (clientApp != null && apiProvisioningConfig.isInjectClientIdSecret()) {
                        HashMap<String, String> clientCreds = new HashMap<>();
                        clientCreds.put(apiProvisioningConfig.getInjectClientIdSecretKey() + ".id", clientApp.getClientId());
                        clientCreds.put(apiProvisioningConfig.getInjectClientIdSecretKey() + ".secret", clientApp.getClientSecret());
                        transformers.add(new SetPropertyTransformer(apiProvisioningConfig.getInjectClientIdSecretFile(), clientCreds));
                    }
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
                }
            }
            return deploymentRequest.deploy(appName, filename, file);
        } finally {
            if (tmpFile) {
                ((TempFile) file).close();
            }
        }
    }
}
