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

@Mojo(name = "provision", defaultPhase = LifecyclePhase.DEPLOY, requiresProject = false)
public class ProvisionAPIMojo extends AbstractMojo {
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
     * If true API provisioning will be skipped
     */
    @Parameter(property = "anypoint.api.provisioning.skip", required = false)
    private boolean skipApiProvisioning;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    }
}
