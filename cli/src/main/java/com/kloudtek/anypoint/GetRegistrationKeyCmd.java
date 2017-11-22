package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;

@Parameters(commandDescription = "Retrieve a server registration key")
public class GetRegistrationKeyCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organizationName"})
    private String organizationName;
    @Parameter(description = "Environment (if not set will use default in profile)", names = {"-e", "--environment"})
    private String environmentName;
    @Parameter(description = "Create organizationName if it doesn't exist", names = {"-co", "--create-organizationName"})
    private boolean createOrganization = false;
    @Parameter(description = "Create environment if it doesn't exist (type will be set to sandbox)", names = {"-ce", "--create-environment"})
    private boolean createEnvironment;

    public void execute(AnypointCli cli) throws NotFoundException, HttpException, IOException {
        AnypointClient client = cli.getClient();
        organizationName = cli.getOrganization(organizationName);
        environmentName = cli.getEnvironment(environmentName);
        Organization organization = null;
        try {
            organization = client.findOrganization(organizationName);
        } catch (NotFoundException e) {
            if (createOrganization) {
                organization = client.createOrganization(organizationName);
            } else {
                throw e;
            }
        }
        Environment environment = null;
        try {
            environment = organization.findEnvironment(environmentName);
        } catch (NotFoundException e) {
            if (createEnvironment) {
                environment = organization.createEnvironment(environmentName, Environment.Type.SANDBOX);
            } else {
                throw e;
            }
        }
        String key = environment.getServerRegistrationKey();
        System.out.println(key);
    }
}
