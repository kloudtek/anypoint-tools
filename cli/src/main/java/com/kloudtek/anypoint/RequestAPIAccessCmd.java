package com.kloudtek.anypoint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;

@Parameters(commandDescription = "Request API Access")
public class RequestAPIAccessCmd {
    @Parameter(description = "Organization (if not set will use default in profile)", names = {"-o", "--organizationName"})
    private String organizationName;
    @Parameter(description = "Name of the client application", names = {"-p", "--application"}, required = true)
    private String clientApplicationName;
    @Parameter(description = "Name of the API to request access from", names = {"-a", "--api"}, required = true)
    private String apiName;
    @Parameter(description = "Version of the API", names = {"-v", "--apiversion"}, required = true)
    private String apiVersionName;
    @Parameter(description = "Automatically approve if required", names = {"-ap", "--approve"})
    private boolean autoApprove = true;
    @Parameter(description = "Automatically restore access if revoked", names = {"-r", "--restore"})
    private boolean autoRestore = true;
    @Parameter(description = "SLA Tier (required if the api version has SLA Tiers assigned)", names = {"-s", "--slatier"})
    private String slaTier;

    public void execute(AnypointCli cli) throws HttpException, IOException, NotFoundException {
        Organization organization = cli.getClient().findOrganization(cli.getOrganizationName(organizationName));
        switch (organization.requestAPIAccess(clientApplicationName, apiName, apiVersionName, autoApprove, autoRestore, slaTier)) {
            case RESTORED:
                cli.println("API access was restored");
                break;
            case GRANTED:
                cli.println("Access granted");
                break;
            case PENDING:
                cli.println("Access requested and pending approval");
                break;
        }
    }
}
