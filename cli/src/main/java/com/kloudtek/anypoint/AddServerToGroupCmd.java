package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.kloudtek.anypoint.runtime.Server;

import java.io.IOException;

@Parameters(commandDescription = "Add a server to a group")
public class AddServerToGroupCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organization"})
    private String organization;
    @Parameter(description = "Environment (if not set will use default in profile)", names = {"-e", "--environment"})
    private String environment;
    @Parameter(description = "Server name")
    private String name;

    public void execute(AnypointCli cli) throws NotFoundException, HttpException, IOException {
        AnypointClient client = cli.getClient();
        organization = cli.getOrganization(organization);
        environment = cli.getEnvironment(environment);
        Environment env = client.findOrganization(organization).findEnvironment(this.environment);
        Server server;
        try {
            server = env.findServer(name);
        } catch (NotFoundException e) {
            System.out.println("Unable to find server " + name + " in org " + organization + " and env " + environment);
            System.exit(-1);
        }
    }
}
