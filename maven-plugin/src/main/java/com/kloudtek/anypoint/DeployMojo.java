package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.runtime.HDeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY, requiresProject = false)
public class DeployMojo extends AbstractMojo {
    /**
     * Anypoint username
     */
    @Parameter(property = "anypoint.username", required = true)
    private String username;
    /**
     * Anypoint password
     */
    @Parameter(property = "anypoint.password", required = true)
    private String password;
    /**
     * Anypoint organization name
     */
    @Parameter(name = "org", property = "anypoint.org", required = true)
    private String org;
    /**
     * Anypoint Environment name
     */
    @Parameter(name = "env", property = "anypoint.env", required = true)
    private String env;
    /**
     * Anypoint target name (Server / Server Group / Cluster)
     */
    @Parameter(name = "target", property = "anypoint.target", required = true)
    private String target;
    /**
     * If true API provisioning will be skipped
     */
    @Parameter(property = "anypoint.api.provisioning.skip", required = false)
    private boolean skipApiProvisioning;
    /**
     * If true deployment will be skipped
     */
    @Parameter(property = "anypoint.deploy.skip", required = false)
    private boolean skipDeploy;
    /**
     * File to deploy (only needed when invoking standalone without a valid pom)
     */
    @Parameter(property = "anypoint.deploy.file", required = false)
    private File file;
    /**
     * Application name
     */
    @Parameter(property = "anypoint.deploy.name", required = false)
    private String appName;
    /**
     * Force deployment even if same already deployed application exists
     */
    @Parameter(property = "anypoint.deploy.force", required = false)
    private boolean force;
    /**
     * If true will skip wait for application to start (successfully or not)
     */
    @Parameter(property = "anypoint.deploy.skipwait", required = false)
    private boolean skipWait;
    /**
     * Deployment timeout
     */
    @Parameter(property = "anypoint.deploy.timeout", required = false)
    private long deployTimeout = 120000L;
    /**
     * Delay (in milliseconds) in retrying a deployment
     */
    @Parameter(property = "anypoint.deploy.retrydelay", required = false)
    private long deployRetryDelay = 2500L;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        if (!skipDeploy) {
            try {
                MavenProject project = (MavenProject) getPluginContext().get("project");
                if (isTemplateOrExample(project) && !force) {
                    log.warn("Project contains mule-application-template or mule-application-example, skipping deployment (use anypoint.deploy.force to force the deployment)");
                    return;
                }
                if (file == null) {
                    log.debug("No deploy file defined");
                    if (project == null) {
                        throw new MojoExecutionException("File not specified while running out of project");
                    }
                    file = getProjectJar(project);
                }
                if (appName == null) {
                    if (project != null) {
                        appName = project.getArtifactId();
                    } else {
                        appName = file.getName();
                    }
                }
                AnypointClient client = new AnypointClient(username, password);
                log.debug("Searching for org " + org);
                Organization o = client.findOrganization(org);
                log.debug("Found org " + org + " : " + o.getId());
                log.debug("Searching for env " + env);
                Environment e = o.findEnvironment(env);
                log.debug("Found env " + env + " : " + e.getId());
                log.debug("Searching for target " + target);
                Server t = e.findServer(target);
                log.debug("Found target " + target + " : " + t.getId());
                log.debug("Deploying " + file.getName());
                APIProvisioningConfig apiProvisioningConfig = skipApiProvisioning ? null : new APIProvisioningConfig();
                HDeploymentResult app = t.deployOnPrem(appName, file, apiProvisioningConfig);
                if (!skipWait) {
                    log.info("Waiting for application start");
                    app.waitDeployed(deployTimeout, deployRetryDelay);
                    log.info("Application started successfully");
                }
                log.info("Deployment completed successfully");
            } catch (Exception e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

    private File getProjectJar(MavenProject project) throws MojoExecutionException {
        if (getLog().isDebugEnabled()) {
            getLog().debug("Listing attached artifacts : " + project.getAttachedArtifacts());
        }
        for (Artifact artifact : project.getAttachedArtifacts()) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("Found : " + artifact.getFile() + " of classifier " + artifact.getClassifier());
            }
            if (artifact.getClassifier().equals("mule-application")) {
                getLog().debug("File is mule-application");
                return artifact.getFile();
            } else if (getLog().isDebugEnabled()) {
                getLog().debug("File is mule-application");
            }
        }
        throw new MojoExecutionException("No mule-application attached artifact found");
    }

    private boolean isTemplateOrExample(MavenProject project) {
        for (Artifact artifact : project.getAttachedArtifacts()) {
            String classifier = artifact.getClassifier();
            if (classifier.equals("mule-application-template") || classifier.equals("mule-application-example")) {
                return true;
            }
        }
        return false;
    }
}
