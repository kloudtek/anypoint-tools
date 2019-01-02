package com.kloudtek.anypoint;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

@Mojo(name = "attach-api-provisioning", requiresProject = false)
public class AttachAPIProvisioningArtifactMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    }
}
