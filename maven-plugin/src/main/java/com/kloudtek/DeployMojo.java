package com.kloudtek;

import com.kloudtek.anypoint.AnypointClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DeployMojo extends AbstractMojo {
    @Parameter(property = "anypoint.file", name = "file")
    private File file;
    @Parameter(property = "anypoint.username", name = "username")
    private String username;
    @Parameter(property = "anypoint.password", name = "password")
    private String password;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (file == null) {
            file = ((MavenProject) getPluginContext().get("project")).getArtifact().getFile();
        }
        AnypointClient anypointClient = new AnypointClient(username, password);
        System.out.println();
    }
}
