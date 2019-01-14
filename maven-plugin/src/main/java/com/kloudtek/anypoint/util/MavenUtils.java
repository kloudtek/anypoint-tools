package com.kloudtek.anypoint.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.repository.AuthenticationSelector;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

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

    public static void addRepositoryUsernamePassword(AuthenticationSelector authenticationSelector, String id, String username, String password) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends AuthenticationSelector> selectorClass = authenticationSelector.getClass();
        if (!selectorClass.getName().equals("org.eclipse.aether.util.repository.DefaultAuthenticationSelector")) {
            throw new IllegalStateException("Unsupported maven authentication selector:" + selectorClass.getName());
        }
        ClassLoader cl = selectorClass.getClassLoader();
        Class<?> authClass = cl.loadClass("org.eclipse.aether.repository.Authentication");
        Class<?> authBuilderClass = cl.loadClass("org.eclipse.aether.util.repository.AuthenticationBuilder");
        Object authBuilder = authBuilderClass.newInstance();
        authBuilderClass.getMethod("addUsername", String.class).invoke(authBuilder, username);
        authBuilderClass.getMethod("addPassword", String.class).invoke(authBuilder, password);
        Object authObj = authBuilderClass.getMethod("build").invoke(authBuilder);
        selectorClass.getMethod("add", String.class, authClass).invoke(authenticationSelector, id, authObj);
    }
}
