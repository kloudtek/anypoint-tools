package com.kloudtek.anypoint;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractOrganizationalMojo extends AbstractMojo {
    /**
     * Anypoint username
     */
    @Parameter(property = "anypoint.username", required = true)
    protected String username;
    /**
     * Anypoint password
     */
    @Parameter(property = "anypoint.password", required = true)
    protected String password;
    /**
     * Anypoint organization name
     */
    @Parameter(name = "org", property = "anypoint.org", required = false)
    protected String org;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            Log log = getLog();
            AnypointClient client = new AnypointClient(username, password);
            log.debug("Searching for org " + org);
            Organization organization = client.findOrganization(org);
            log.debug("Found org " + organization + " : " + organization.getId());
            execute(client, organization);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public abstract void execute(AnypointClient client, Organization org) throws Exception;
}
