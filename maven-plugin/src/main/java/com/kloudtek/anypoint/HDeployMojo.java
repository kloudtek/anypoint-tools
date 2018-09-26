package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;

/**
 * Deploy application to on-premise runtimes (hydrid model) via anypoint
 */
@Mojo(name = "hdeploy", requiresProject = false)
public class HDeployMojo extends AbstractDeployMojo {
    /**
     * Anypoint target name (Server / Server Group / Cluster)
     */
    @Parameter(name = "target", property = "anypoint.target", required = true)
    private String target;
    /**
     * Properties to be injected into the archive (properties resulting from API provisioning will be included in those
     * properties)
     */
    @Parameter(property = "anypoint.deploy.retrydelay", required = false)
    protected Map<String, String> properties;
    /**
     * Name of file which will contain injected properties
     */
    @Parameter(property = "anypoint.deploy.retrydelay", required = false)
    protected String propertiesFilename = "deployconfig.properties";

    @Override
    protected DeploymentResult deploy(Environment environment, APIProvisioningConfig apiProvisioningConfig) throws MojoExecutionException, HttpException {
        try {
            Server server = environment.findServer(target);
            APIProvisioningConfig config = new APIProvisioningConfig();
//            new HDeploymentRequest(server, appName, file, filename,  )
        } catch (NotFoundException e) {
            throw new MojoExecutionException("Target " + target + " not found in env " + env + " in business group " + org);
        }
        return null;
    }
}
