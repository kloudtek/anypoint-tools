package com.kloudtek.anypoint;

import com.kloudtek.anypoint.provision.ProvisioningConfig;
import com.kloudtek.anypoint.runtime.Application;
import com.kloudtek.anypoint.runtime.ApplicationDeploymentFailedException;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.util.TempFile;
import com.kloudtek.util.UnexpectedException;
import com.kloudtek.util.UserDisplayableException;
import com.kloudtek.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Command(name = "deploy", description = "Deploy Application", sortOptions = false)
public class DeployApplicationCmd extends AbstractEnvironmentCmd {
    private static final Logger logger = LoggerFactory.getLogger(DeployApplicationCmd.class);
    @Option(description = "Target", names = {"-t", "--target"}, required = true)
    protected String target;
    @Option(description = "Application names (there must be one per file specified, that will be used in the same order (first name = first file)", names = {"-n", "--name"}, required = true)
    protected List<String> appNames;
    @Option(description = "Application files", names = {"-f", "--file"}, required = true)
    protected List<File> appArchives;
    @Option(description = "Force deployment even if app is unchanged", names = {"--force"})
    protected boolean force = false;
    @Option(description = "Don't wait for application start", names = {"-sw", "--skip-wait-started"})
    private boolean skipWait;
    @Option(description = "Skip anypoint provisioning", names = {"-sp", "--skip-provisioning"})
    private boolean skipProvisioning;
    @Option(description = "Environment suffix (will create a variable 'env' which will be appended to all provisioned API versions and all client application names)", names = {"-s", "--envsuffix"})
    private String envSuffix;
    @Option(description = "Enable legacy mode for older style anypoint.json", hidden = true, names = {"--legacymode"})
    private boolean legacyMode;
    @Option(names = {"-ab", "--accessed-by"}, description = "Extra client applications which should be granted access to all APIs in the application (note envSuffix will not be automatically applied to those)")
    private List<String> extraAccess;
    @Option(names = {"-tr", "--transform"}, description = "json configuration for a package transformer")
    private List<String> transforms;
    @Option(names = "-D", description = "Provisioning parameters")
    private Map<String, String> provisioningParams = new HashMap<>();

    @Override
    protected void execute(Environment environment) throws Exception {
        ExecutorService deployThreadPool = Executors.newCachedThreadPool();
        Server server = environment.findServer(target);
        HashMap<String, Application> deployed = new HashMap<>();
        for (File appArchive : appArchives) {
            if (!appArchive.exists()) {
                logger.error("Application file not found: " + appArchive.getPath());
            }
        }
        int appCount = appArchives.size();
        if (appNames.size() != appCount) {
            throw new UserDisplayableException("Number of names do not match number of files specified");
        }
        ArrayList<String> failed = new ArrayList<>(deployed.size());
        ArrayList<Future> tasks = new ArrayList<>();
        for (int i = 0; i < appCount; i++) {
            final int idx = i;
            tasks.add(deployThreadPool.submit(() -> {
                File appArch = appArchives.get(idx);
                String appName = appNames.get(idx);
                try {
                    if (appArch.exists()) {
                        if (!skipProvisioning) {
                            ProvisioningConfig provisioningConfig = new ProvisioningConfig(provisioningParams, extraAccess);
                            if (legacyMode) {
                                provisioningConfig.setLegacyMode(true);
                                provisioningConfig.setDescriptorLocation("classes/anypoint.json");
                            }
                            logger.info("Provisioning: " + appName);
                            TransformList transformList = parent.getClient().provision(server.getParent().getParent(), appArch, provisioningConfig, envSuffix);
                            if (transformList != null) {
                                try {
                                    appArch = transformList.applyTransforms(appArch, null);
                                } catch (Exception e) {
                                    throw new UserDisplayableException("An error occured while applying application " + appName + " transformations: " + e.getMessage(), e);
                                }
                            }
                            logger.info("Provisioning completed: " + appName);
                        }
                        if (!force) {
                            if (server.checkApplicationExist(appName, appArch, true)) {
                                logger.info("Application already deployed: " + appName);
                                return;
                            }
                        }
                        logger.info("Deploying application: " + appName);
                        try {
                            Application application = server.deploy(appName, appArch);
                            deployed.put(appArch.getName(), application);
                        } catch (IOException e) {
                            throw new UserDisplayableException("Error loading application " + appName + " : " + e.getMessage(), e);
                        }
                    } else {
                        throw new UserDisplayableException("File not found: " + appArch.getPath());
                    }
                } catch (Exception e) {
                    logger.error("Failed to deploy " + appName + " : " + e.getMessage(), e);
                    failed.add(appName);
                } finally {
                    if (appArchives instanceof TempFile) {
                        IOUtils.close((TempFile) appArch);
                    }
                }
            }));
        }
        for (Future task : tasks) {
            task.get();
        }
        tasks.clear();
        logger.info("Applications deployment completed");
        if (!skipWait) {
            logger.info("Waiting for apps to start");
            for (Map.Entry<String, Application> entry : deployed.entrySet()) {
                tasks.add(deployThreadPool.submit(() -> {
                    try {
                        entry.getValue().waitDeployed();
                    } catch (ApplicationDeploymentFailedException e) {
                        logger.error("Failed to deploy " + entry.getKey() + " : " + e.getMessage(), e);
                        failed.add(entry.getKey());
                    }
                    return null;
                }));
            }
            for (Future task : tasks) {
                task.get();
            }
        }
        int failedCount = failed.size();
        if (failedCount == 0) {
            logger.info("All apps successfully started");
        } else {
            throw new UnexpectedException((appCount - failedCount) + " out of " + appCount + " apps started successfully. Failed apps: " + failed);
        }
    }
}
