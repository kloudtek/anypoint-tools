package com.kloudtek.anypoint;

import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractOrganizationalMojo extends AbstractAnypointMojo {
    private Organization organization;
    /**
     * Anypoint organization name
     */
    @Parameter(property = "anypoint.org")
    protected String org;

    public synchronized Organization getOrganization() throws NotFoundException, HttpException {
        if (organization == null) {
            organization = getClient().findOrganization(org);
        }
        return organization;
    }
}
