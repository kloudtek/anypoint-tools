package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.kloudtek.anypoint.provision.InvalidAnypointDescriptorException;

import java.io.IOException;

public abstract class AbstractEnvironmentCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organizationName"})
    protected String organizationName;
    @Parameter(description = "Environment (if not set will use default in profile)", names = {"-e", "--environment"})
    protected String environmentName;
    @Parameter(description = "Create organizationName if it doesn't exist", names = {"-co", "--create-organizationName"})
    protected boolean createOrganization = false;
    @Parameter(description = "Create environment if it doesn't exist (type will be set to sandbox)", names = {"-ce", "--create-environment"})
    protected boolean createEnvironment;
    @Parameter(description = "Environment type if creation required", names = {"-et", "--env-type"})
    protected Environment.Type createEnvironmentType = Environment.Type.SANDBOX;

    public void execute(AnypointCli cli) throws NotFoundException, HttpException, IOException, InvalidAnypointDescriptorException {
        AnypointClient client = cli.getClient();
        Environment environment = client.findEnvironment(cli.getOrganizationName(organizationName), cli.getEnvironmentName(environmentName), createOrganization, createEnvironment, createEnvironmentType);
        execute(cli, environment);
    }

    protected abstract void execute(AnypointCli cli, Environment environment) throws HttpException, NotFoundException, InvalidAnypointDescriptorException;
}
