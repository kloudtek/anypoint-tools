package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.deploy.ApplicationSource;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.util.MavenUtils;
import com.kloudtek.util.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDeployMojo extends AbstractEnvironmentalMojo {
    /**
     * If true API provisioning will be skipped
     */
    @Parameter(property = "anypoint.api.provisioning.skip")
    protected boolean skipApiProvisioning;
    /**
     * If true deployment will be skipped
     */
    @Parameter(property = "anypoint.deploy.skip")
    protected boolean skipDeploy;
    /**
     * File to deploy (only needed when invoking standalone without a valid pom). To deploy from exchange use uri in the format
     * of <pre>exchange://[orgId]:[groupId]:[artifactId]:[version]</pre> or <pre>exchange://[groupId]:[artifactId]:[version]</pre>
     */
    @Parameter(property = "anypoint.deploy.file")
    protected String file;
    /**
     * Filename (if not specified the file's name will be used)
     */
    @Parameter(property = "anypoint.deploy.filename")
    protected String filename;
    /**
     * Application name
     */
    @Parameter(property = "anypoint.deploy.name")
    protected String appName;
    /**
     * Force deployment even if same already deployed application exists
     */
    @Parameter(property = "anypoint.deploy.force")
    protected boolean force;
    /**
     * If true will skip wait for application to start (successfully or not)
     */
    @Parameter(property = "anypoint.deploy.skipwait")
    protected boolean skipWait;
    /**
     * Deployment timeout
     */
    @Parameter(property = "anypoint.deploy.timeout")
    protected long deployTimeout = TimeUnit.MINUTES.toMillis(10);
    /**
     * Delay (in milliseconds) in retrying a deployment
     */
    @Parameter(property = "anypoint.deploy.retrydelay")
    protected long deployRetryDelay = 2500L;

    protected ApplicationSource source;

    @Override
    protected void doExecute() throws Exception {
        if (!skipDeploy) {
            MavenProject project = (MavenProject) getPluginContext().get("project");
            if (project.getArtifactId().equals("standalone-pom") && project.getGroupId().equals("org.apache.maven")) {
                project = null;
            }
            if (MavenUtils.isTemplateOrExample(project) && !force) {
                logger.warn("Project contains mule-application-template or mule-application-example, skipping deployment (use anypoint.deploy.force to force the deployment)");
                return;
            }
            if (file == null) {
                logger.debug("No deploy file defined");
                if (project == null) {
                    throw new MojoExecutionException("File not specified while running out of project");
                }
                file = MavenUtils.getProjectJar(project, logger).getPath();
            }
            source = ApplicationSource.create(getEnvironment().getOrganization().getId(), getClient(), file);
            try {
                if (filename == null) {
                    filename = source.getFileName();
                }
                if (appName == null) {
                    if (project != null) {
                        appName = project.getArtifactId();
                    } else {
                        appName = source.getArtifactId();
                    }
                }
                APIProvisioningConfig apiProvisioningConfig = skipApiProvisioning ? null : new APIProvisioningConfig();
                DeploymentResult app = deploy(getEnvironment(), apiProvisioningConfig);
                if (!skipWait) {
                    logger.info("Waiting for application start");
                    app.waitDeployed(deployTimeout, deployRetryDelay);
                    logger.info("Application started successfully");
                }
                logger.info("Deployment completed successfully");
            } finally {
                IOUtils.close(source);
            }
        }
    }

    protected abstract DeploymentResult deploy(Environment env, APIProvisioningConfig apiProvisioningConfig) throws Exception;
}
