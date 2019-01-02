package com.kloudtek.anypoint.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;

public class MavenUtils {
    public static File getProjectJar(MavenProject project, Log log) throws MojoExecutionException {
        if (log.isDebugEnabled()) {
            log.debug("Listing attached artifacts : " + project.getAttachedArtifacts());
        }
        for (Object artifactObj : project.getAttachedArtifacts()) {
            Artifact artifact = (Artifact) artifactObj;
            if (log.isDebugEnabled()) {
                log.debug("Found : " + artifact.getFile() + " of classifier " + artifact.getClassifier());
            }
            if (artifact.getClassifier().equals("mule-application")) {
                log.debug("File is mule-application");
                return artifact.getFile();
            } else if (log.isDebugEnabled()) {
                log.debug("File is not mule-application");
            }
        }
        throw new MojoExecutionException("No mule-application attached artifact found");
    }

    public static boolean isTemplateOrExample(MavenProject project) {
        if (project != null) {
            for (Object artifactObj : project.getAttachedArtifacts()) {
                Artifact artifact = (Artifact) artifactObj;
                String classifier = artifact.getClassifier();
                if (classifier.equals("mule-application-template") || classifier.equals("mule-application-example")) {
                    return true;
                }
            }
        }
        return false;
    }
}
