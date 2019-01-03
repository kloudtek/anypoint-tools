package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.deploy.ApplicationSource;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.util.UserDisplayableException;
import com.kloudtek.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDeployApplicationCmd extends AbstractEnvironmentCmd {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDeployApplicationCmd.class);
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    protected boolean usageHelpRequested;
    /**
     * If true API provisioning will be skipped
     */
    @Option(description = "Skip API Provisioning", names = {"-sp", "--skip-api-provisioning"})
    protected boolean skipApiProvisioning = false;
     /**
     * File to deploy. This can either be a filename, or an uri in the format of exchange://[orgId]:[groupId]:[artifactId]:[version]
      * or exchange://[groupId]:[artifactId]:[version], or exchange://[artifactId]:[version]
     */
    @Parameters(description = "Application archive file", index = "0")
    protected String sourcePath;
    protected ApplicationSource source;
    /**
     * Application name
     */
    @Parameters(description = "Application name", index = "1")
    protected String appName;
    /**
     * Force deployment even if same already deployed application exists
     */
    @Option(description = "Force deployment even if the same (with matching checksum) application already exists", names = {"-fd", "--force-deploy"})
    protected boolean force = false;
    /**
     * If true will skip wait for application to start (successfully or not)
     */
    @Option(description = "Skip waiting for application to successfully start", names = {"-sw", "--skip-wait"})
    protected boolean skipWait = false;
    /**
     * Deployment timeout
     */
    @Option(description = "Maximum time to wait for application to start successfully (in milliseconds)", names = {"-wst", "--wait-start-timeout"})
    protected long waitForStartTimeout = TimeUnit.MINUTES.toMillis(10);
    @Option(description = "Delay between deployment retries (set to zero for no retry)", names = {"-rd", "--redeploy-delay"})
    protected long redeployDelay = 2500L;
    @Option(names = "-V", description = "API Provisioning variable")
    protected Map<String, String> provisioningVars;
    @Option(names = "-P", description = "Application property")
    protected Map<String, String> appProperties;
    @Option(names = "-ab", description = "Client application that should be given access right to this application")
    protected List<String> accessedBy;
    @Option(names = "-fn", description = "File name (is not set, it will use the archive's file name")
    protected String filename;
    protected APIProvisioningConfig apiProvisioningConfig;

    @Override
    protected void execute(Environment environment) throws Exception {
        if (sourcePath == null) {
            throw new UserDisplayableException("File to deploy not specified");
        }
        source = ApplicationSource.create(environment.getOrganization().getId(),environment.getClient(),sourcePath);
        try {
            if (filename == null) {
                filename = source.getFileName();
            }
            apiProvisioningConfig = skipApiProvisioning ? null : new APIProvisioningConfig();
            if (apiProvisioningConfig != null) {
                apiProvisioningConfig.setVariables(provisioningVars);
                apiProvisioningConfig.setAccessedBy(accessedBy);
            }
            DeploymentResult app = deploy(environment);
            if (!skipWait) {
                app.waitDeployed(waitForStartTimeout, redeployDelay);
                logger.info("Application started successfully");
            }
            logger.info("Deployment completed successfully");
        } finally {
            IOUtils.close(source);
        }
    }

    protected abstract DeploymentResult deploy(Environment environment) throws ProvisioningException, IOException, HttpException, NotFoundException;
}
