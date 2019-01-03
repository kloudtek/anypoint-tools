package com.kloudtek.anypoint;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Requirement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Prepare a project for deployment on anypoint exchange.
 */
@Mojo(name = "prepare-project", requiresProject = false, defaultPhase = LifecyclePhase.VALIDATE)
public class PrepareProjectMojo extends AbstractOrganizationalMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    /**
     * Maven exchange domain (change to '<pre>maven.eu1.anypoint.mulesoft.com</pre>' for europe anypoint)
     */
    @Parameter(defaultValue = "maven.anypoint.mulesoft.com")
    private String mavenExchangeDomain;
    @Parameter(defaultValue = "true")
    private boolean updateGroupId;
    @Parameter(defaultValue = "true", property = "anypoint.prepare.updateVersionIfSnapshot")
    private boolean updateVersionIfSnapshot;
    @Parameter(defaultValue = "true")
    private boolean addDistributionManagement;
    @Requirement
    private LegacySupport legacySupport;

    @Override
    public void execute(AnypointClient client, Organization organization) throws Exception {
        if (updateGroupId) {
            String groupId = organization.getId();
            project.setGroupId(groupId);
            if (project.getAttachedArtifacts() != null) {
                for (Artifact attachedArtifact : project.getAttachedArtifacts()) {
                    attachedArtifact.setGroupId(groupId);
                }
            }
            if (project.getArtifact() != null) {
                project.getArtifact().setGroupId(groupId);
            }
        }
        if (updateVersionIfSnapshot && project.getArtifact() != null && project.getArtifact().isSnapshot()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String version = project.getVersion() + "-" + timestamp;
            project.setVersion(version);
//            if (project.getAttachedArtifacts() != null) {
//                for (Artifact attachedArtifact : project.getAttachedArtifacts()) {
//                    attachedArtifact.setVersion(version);
//                }
//            }
            if (project.getArtifact() != null) {
                project.getArtifact().setVersion(version);
                project.getArtifact().setVersionRange(VersionRange.createFromVersion(version));
            }
        }
        if (addDistributionManagement) {
            MavenArtifactRepository repo = new MavenArtifactRepository();
            repo.setAuthentication(new org.apache.maven.artifact.repository.Authentication(username, password));
            repo.setId("exchange-maven-" + organization.getId());
            repo.setUrl("https://" + mavenExchangeDomain + "/api/v1/organizations/" + organization.getId() + "/maven");
            repo.setLayout(new DefaultRepositoryLayout());
            project.setReleaseArtifactRepository(repo);
        }
    }
}
