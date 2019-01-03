package com.kloudtek.anypoint;

import com.kloudtek.util.UserDisplayableException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractEnvironmentalMojo extends AbstractOrganizationalMojo {
    /**
     * Anypoint Environment name
     */
    @Parameter(property = "anypoint.env", required = true)
    protected String env;

    @Override
    public void execute(AnypointClient client, Organization organization) throws Exception {
        try {
            Environment environment = organization.findEnvironmentByName(env);
            execute(client, environment);
        } catch (NotFoundException e) {
            throw new UserDisplayableException("Unable to find environment " + env + " in org " + organization.getName());
        }
    }

    public abstract void execute(AnypointClient client, Environment env) throws Exception;
}
