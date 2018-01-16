package com.kloudtek.anypoint;

import com.kloudtek.anypoint.provision.ProvisioningConfig;
import com.kloudtek.anypoint.provision.TransformList;
import com.kloudtek.anypoint.runtime.Application;
import com.kloudtek.anypoint.runtime.ApplicationDeploymentFailedException;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.util.TempFile;
import com.kloudtek.util.UserDisplayableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(name = "deploy", description = "Deploy Application", sortOptions = false)
public class DeployApplicationCmd extends AbstractEnvironmentCmd {
    private static final Logger logger = LoggerFactory.getLogger(DeployApplicationCmd.class);
    @Option(description = "Target", names = {"-t", "--target"}, required = true)
    protected String target;
    @Option(description = "Application name", names = {"-n", "--name"}, required = true)
    protected String appName;
    @Option(description = "Application file", names = {"-f", "--file"}, required = true)
    protected File appArch;
    @Option(description = "Force deployment even if app is unchanged", names = {"--force"})
    protected boolean force = false;
    @Option(description = "Wait for application start", names = {"-w", "--waitstarted"})
    private boolean waitAppStarted;
    @Option(description = "Provision anypoint", names = {"-p", "--provisionanypoint"})
    private boolean provisionAnypoint;
    @Option(description = "Environment suffix (will be appended to API versions and client application names)", names = {"-s", "--envsuffix"})
    private String envSuffix;
    @Option(description = "Enable legacy mode for older style anypoint.json", hidden = true, names = {"--legacymode"})
    private boolean legacyMode;
    @Option(names = {"-tr", "--transform"}, description = "json configuration for a package transformer")
    private List<String> transforms;
    @Option(names = "-D", description = "Provisioning parameters")
    private Map<String, String> provisioningParams = new HashMap<>();

    @Override
    protected void execute(Environment environment) throws Exception {
        Server server = environment.findServer(target);
        try {
            if (appArch.exists()) {
                try {
                    if (provisionAnypoint) {
                        ProvisioningConfig provisioningConfig = new ProvisioningConfig(provisioningParams);
                        if (legacyMode) {
                            provisioningConfig.setLegacyMode(true);
                            provisioningConfig.setDescriptorLocation("classes/anypoint.json");
                        }
                        logger.info("Provisioning anypoint");
                        TransformList transformList = parent.getClient().provision(server.getParent().getParent(), appArch, provisioningConfig, envSuffix);
                        if (transformList != null) {
                            try {
                                appArch = transformList.applyTransforms(appArch, null);
                            } catch (Exception e) {
                                throw new UserDisplayableException("An error occured while applying application ctransformations: " + e.getMessage(), e);
                            }
                        }
                        logger.info("Provisioning completed");
                    }
                    if (!force) {
                        if (server.checkApplicationExist(appName, appArch, true)) {
                            logger.info("Application already deployed");
                            return;
                        }
                    }
                    logger.info("Deploying application");
                    Application deploy = server.deploy(appName, appArch);
                    logger.info("Application deployment completed");
                    if (waitAppStarted) {
                        logger.info("Waiting for app to start ... ");
                        deploy.waitDeployed();
                        logger.info("app is started");
                    }
                } catch (IOException e) {
                    throw new UserDisplayableException("Error loading application file: " + e.getMessage(), e);
                } catch (ApplicationDeploymentFailedException e) {
                    throw new UserDisplayableException("Application deployment failed: " + e.getMessage(), e);
                }
            } else {
                throw new UserDisplayableException("File not found: " + appArch.getPath());
            }
        } finally {
            if (appArch instanceof TempFile) {
                ((TempFile) appArch).close();
            }
        }
    }
}
