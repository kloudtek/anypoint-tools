package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.APIVersion;

import java.io.IOException;

@Parameters(commandDescription = "Request API Access")
public class RequestAPIAccessCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organizationName"})
    private String organizationName;
    @Parameter(description = "Name of the client application", names = {"-p", "--application"})
    private String clientApplicationName;
    @Parameter(description = "Name of the API to request access from", names = {"-a", "--api"})
    private String apiName;
    @Parameter(description = "Version of the API", names = {"-v", "--apiversion"})
    private String apiVersion;


    public void execute(AnypointCli cli) throws NotFoundException, HttpException, IOException {
        AnypointClient client = cli.getClient();
        organizationName = cli.getOrganization(organizationName);
        Organization organization = client.findOrganization(organizationName);
        API api = organization.getAPI(apiName);
        APIVersion v = api.getVersion(apiVersion);
    }
}
