package com.kloudtek.anypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "reqapiaccess", description = "Request API Access", sortOptions = false)
public class RequestAPIAccessCmd extends AbstractOrganizationalCmd {
    private static final Logger logger = LoggerFactory.getLogger(RequestAPIAccessCmd.class);
    @Option(description = "Name of the client application", names = {"-c", "--client-application"}, required = true)
    private String clientApplicationName;
    @Option(description = "Name of the API to request access from", names = {"-a", "--api"}, required = true)
    private String apiName;
    @Option(description = "Version of the API", names = {"-v", "--apiversion"}, required = true)
    private String apiVersionName;
    @Option(description = "If flag set it will not automatically approve if required", names = {"-ap", "--approve"})
    private boolean autoApprove = true;
    @Option(description = "If flag is set, it will not automatically restore access if revoked", names = {"-r", "--restore"})
    private boolean autoRestore = true;
    @Option(description = "SLA Tier (required if the api version has SLA Tiers assigned)", names = {"-s", "--slatier"})
    private String slaTier;

    @Override
    protected void execute(Organization organization) throws Exception {
        switch (organization.requestAPIAccess(clientApplicationName, apiName, apiVersionName, autoApprove, autoRestore, slaTier)) {
            case RESTORED:
                logger.info("API access was restored");
                break;
            case GRANTED:
                logger.info("Access granted");
                break;
            case PENDING:
                logger.info("Access requested and pending approval");
                break;
        }
    }
}
