package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.runtime.Application;
import com.kloudtek.anypoint.runtime.ApplicationDeploymentFailedException;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.unpack.FileType;
import com.kloudtek.unpack.Unpacker;
import com.kloudtek.unpack.transformer.Transformer;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Command(name = "deploy", description = "Deploy Application", sortOptions = false)
public class DeployApplicationCmd extends AbstractEnvironmentCmd {
    private static final Logger logger = LoggerFactory.getLogger(DeployApplicationCmd.class);
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    boolean usageHelpRequested;
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
    @Option(description = "Skip anypoint.sh provisioning", names = {"-sp", "--skip-provisioning"})
    private boolean skipProvisioning;
    @Option(description = "Environment suffix (will create a variable 'env' which will be appended to all provisioned API versions and all client application names)", names = {"-s", "--envsuffix"})
    private String envSuffix;
    @Option(names = {"-ab", "--accessed-by"}, description = "Extra client applications which should be granted access to all APIs in the application (note envSuffix will not be automatically applied to those)")
    private List<String> extraAccess;
//    @Option(names = {"-tr", "--transform"}, description = "json configuration for a package transformer")
//    private List<String> transforms;
    @Option(names = "-D", description = "Provisioning parameters")
    private Map<String, String> provisioningParams = new HashMap<>();

    @Override
    protected void execute(Environment environment) throws Exception {
        ExecutorService deployThreadPool = Executors.newCachedThreadPool();
        Server server = environment.findServer(target);
        HashMap<String, DeploymentResult> deployed = new HashMap<>();
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
                String appArchFileName = appArch.getName();
                String appName = appNames.get(idx);
                try {
                    if (appArch.exists()) {
                        if (!skipProvisioning) {
                            throw new UserDisplayableException("Provisioning not supported for command line right now, please use maven plugin or skip provisioning or wait for next release");
//                            APIProvisioningConfig APIProvisioningConfig = new APIProvisioningConfig(provisioningParams, extraAccess);
//                            logger.info("Provisioning: " + appName);
//                            List<Transformer> transformers = parent.getClient().provision(server.getParent().getParent(), appArch, APIProvisioningConfig, envSuffix);
//                            if (transformers != null && !transformers.isEmpty()) {
//                                try {
//                                    File oldAppArch = appArch;
//                                    appArch = new TempFile(appArch.getName(), ".zip");
//                                    Unpacker unpacker = new Unpacker(oldAppArch, FileType.ZIP, appArch, FileType.ZIP);
//                                    unpacker.addTransformers(transformers);
//                                    unpacker.unpack();
//                                } catch (Exception e) {
//                                    throw new UserDisplayableException("An error occured while applying application " + appName + " transformations: " + e.getMessage(), e);
//                                }
//                            }
//                            logger.info("Provisioning completed: " + appName);
                        }
                        if (!force) {
                            if (server.checkApplicationExist(appName, appArch, true)) {
                                logger.info("Application already deployed: " + appName);
                                return;
                            }
                        }
                        logger.info("Deploying application: " + appName);
                        try {
                            DeploymentResult application = server.deploy(appName, appArch, appArchFileName, null);
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
                    if (appArch instanceof TempFile) {
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
            for (Map.Entry<String, DeploymentResult> entry : deployed.entrySet()) {
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
