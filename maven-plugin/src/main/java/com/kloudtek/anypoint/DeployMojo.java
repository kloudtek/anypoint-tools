package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.deploy.CHDeploymentRequest;
import com.kloudtek.anypoint.deploy.HDeploymentRequest;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.anypoint.util.MavenUtils;
import com.kloudtek.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Mojo(name = "deploy", requiresProject = false)
public abstract class DeployMojo extends AbstractDeployMojo {
    /**
     * Anypoint target name (Server / Server Group / Cluster). If not set will deploy to Cloudhub
     */
    @Parameter(name = "target", property = "anypoint.target")
    private String target;

    /**
     * Properties to be injected into the archive (properties resulting from API provisioning will be included in those
     * properties)
     */
    @Parameter(property = "anypoint.deploy.properties", required = false)
    protected Map<String, String> properties;

    /**
     * Hybrid/Onprem only: Name of file which will contain injected properties
     */
    @Parameter(property = "anypoint.deploy.properties.file", required = false)
    protected String propertiesFilename = "deployconfig.properties";

    /**
     * Cloudhub only: Mule version name (will default to latest if not set)
     */
    @Parameter(name = "muleVersionName", property = "anypoint.deploy.ch.muleversion", required = false)
    private String muleVersionName;

    /**
     * Cloudhub only: Deployment region
     */
    @Parameter(name = "region", property = "anypoint.deploy.ch.region", required = false)
    private String region;

    /**
     * Cloudhub only: Worker type (will default to smallest if not specified)
     */
    @Parameter(name = "workerType", property = "anypoint.deploy.ch.worker.type", required = false)
    private String workerType;

    /**
     * Cloudhub only: Worker count (will default to one if not specified).
     */
    @Parameter(name = "workerCount", property = "anypoint.deploy.ch.worker.count")
    private Integer workerCount;

    /**
     * Cloudhub only: If true custom log4j will be used (and cloudhub logging disabled)
     */
    @Parameter(name = "customlog4j", property = "anypoint.deploy.ch.customlog4j")
    private boolean customlog4j;

    @SuppressWarnings("Duplicates")
    @Override
    protected DeploymentResult deploy(Environment environment, APIProvisioningConfig apiProvisioningConfig) throws MojoExecutionException, HttpException {
        if(StringUtils.isBlank(target)) {
            if( workerCount == null ) {
                workerCount = 1;
            }
            try {
                if( customlog4j ) {
                    apiProvisioningConfig.setCustomLog4j(customlog4j);
                }
                return new CHDeploymentRequest(muleVersionName, region, workerType, workerCount, environment, appName, file, filename, properties, apiProvisioningConfig).deploy();
            } catch (ProvisioningException | IOException | NotFoundException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        } else {
            try {
                Server server = environment.findServerByName(target);
                return new HDeploymentRequest(server, appName, file, filename, properties, apiProvisioningConfig).deploy();
            } catch (NotFoundException e) {
                throw new MojoExecutionException("Target " + target + " not found in env " + env + " in business group " + org);
            } catch (ProvisioningException | IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }
}
