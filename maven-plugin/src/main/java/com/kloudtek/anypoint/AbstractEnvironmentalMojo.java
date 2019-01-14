package com.kloudtek.anypoint;

import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractEnvironmentalMojo extends AbstractOrganizationalMojo {
    private Environment environment;
    /**
     * Anypoint Environment name
     */
    @Parameter(property = "anypoint.env", required = true)
    protected String env;

    public synchronized Environment getEnvironment() throws NotFoundException, HttpException {
        if (environment == null) {
            environment = getOrganization().findEnvironmentByName(env);
        }
        return environment;
    }
}
