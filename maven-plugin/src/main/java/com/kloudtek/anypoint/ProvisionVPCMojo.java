package com.kloudtek.anypoint;

import com.kloudtek.util.UserDisplayableException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "provisionvpc", requiresProject = false)
public class ProvisionVPCMojo extends AbstractOrganizationalMojo {
    /**
     * Delete pre-existing VPC with same name (and all applications in associated environments) if it exists prior to creation
     */
    @Parameter(name = "delete", property = "anypoint.vpc.delete")
    private boolean delete;
    /**
     * VPC descriptor file
     */
    @Parameter(name = "file", property = "anypoint.vpc.file", required = true)
    private File file;

    @Override
    public void execute(AnypointClient client, Organization organization) throws Exception {
        getLog().info("Provisioning VPC");
        if (!file.exists()) {
            throw new UserDisplayableException("VPC descriptor file not found: " + file.getPath());
        }
        organization.provisionVPC(file, delete);
        getLog().info("VPC Provisioning complete");
    }
}
