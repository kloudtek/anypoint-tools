package com.kloudtek.anypoint;

import com.kloudtek.anypoint.runtime.Server;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo( name = "deploy", defaultPhase = LifecyclePhase.DEPLOY, requiresProject = false)
@Execute(phase = LifecyclePhase.DEPLOY)
public class DeployMojo extends AbstractMojo {
    @Parameter( property = "anypoint.username", required = true )
    private String username;
    @Parameter( property = "anypoint.password", required = true )
    private String password;
    @Parameter( name = "org", property = "anypoint.org", required = true )
    private String org;
    @Parameter( name = "env", property = "anypoint.env", required = true )
    private String env;
    @Parameter( name = "target", property = "anypoint.target", required = true )
    private String target;
    @Parameter( property = "anypoint.deploy.skip", required = false )
    private boolean skipDeploy;
    @Parameter( property = "anypoint.deploy.file", required = false )
    private File file;
    @Parameter( property = "anypoint.deploy.name", required = false )
    private String appName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if( ! skipDeploy ) {
            try {
                MavenProject project = (MavenProject) getPluginContext().get("project");
                if( file == null ) {
                    if( project == null ) {
                        throw new MojoExecutionException("File not specified while running out of project");
                    }
                    file = getProjectJar(project);
                }
                if( appName == null ) {
                    if( project != null ) {
                        appName = project.getArtifactId();
                    } else {
                        appName = file.getName();
                    }
                }
                AnypointClient client = new AnypointClient(username, password);
                Organization o = client.findOrganization(org);
                Environment e = o.findEnvironment(env);
                Server t = e.findServer(target);
                t.deploy(appName,file);
            } catch (Exception e) {
                throw new MojoExecutionException(e.getMessage(),e);
            }
        }
    }

    private File getProjectJar(MavenProject project) throws MojoExecutionException {
        for (Artifact artifact : project.getAttachedArtifacts()) {
            if( artifact.getClassifier().equals("mule-application") ) {
                return artifact.getFile();
            }
        }
        throw new MojoExecutionException("No mule-application attached artifact found");
    }
}
