package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.anypoint.runtime.ServerGroup;

import java.io.IOException;

@Parameters(commandDescription = "Add a server to a group")
public class AddServerToGroupCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organization"})
    private String organization;
    @Parameter(description = "Environment (if not set will use default in profile)", names = {"-e", "--environment"})
    private String environment;
    @Parameter(description = "Server name", names = {"-n", "--name"}, required = true)
    private String serverName;
    @Parameter(description = "Server group/cluster name", names = {"-g", "--group"}, required = true)
    private String groupName;

    public void execute(AnypointCli cli) throws NotFoundException, HttpException, IOException {
        AnypointClient client = cli.getClient();
        organization = cli.getOrganization(organization);
        environment = cli.getEnvironment(environment);
        Environment env = client.findOrganization(organization).findEnvironment(this.environment);
        Server server = null;
        try {
            server = env.findServer(serverName);
        } catch (NotFoundException e) {
            System.out.println("Unable to find server " + serverName + " in org " + organization + " and env " + environment);
            System.exit(-1);
        }
        ServerGroup group = null;
        try {
            Server groupTmp = env.findServer(groupName);
            if (groupTmp instanceof ServerGroup) {
                group = (ServerGroup) groupTmp;
            } else {
                System.out.println("Server group " + groupName + " is a server, not a group or cluster");
                System.exit(-1);
            }
        } catch (NotFoundException e) {
            System.out.println("Unable to find server group/cluster " + groupName + " in org " + organization + " and env " + environment);
            System.exit(-1);
        }
        group.addServer(server);
    }
}
