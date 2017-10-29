package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;

@Parameters(commandDescription = "Retrieve a server registration key")
public class GetRegistrationKeyCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organization"})
    private String organization;
    @Parameter(description = "Environment (if not set will use default in profile)", names = {"-e", "--environment"})
    private String environment;

    public void execute(AnypointCli cli) throws NotFoundException, HttpException, IOException {
        AnypointClient client = cli.getClient();
        organization = cli.getOrganization(organization);
        environment = cli.getEnvironment(environment);
        String key = client.findOrganization(organization).findEnvironment(environment).getServerRegistrationKey();
        System.out.println(key);
    }
}
