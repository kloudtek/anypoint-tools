package com.kloudtek.anypoint;

import com.kloudtek.ktcli.CliCommand;
import com.kloudtek.ktcli.CliHelper;
import com.kloudtek.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;

@Command(name = "config", description = "Update configuration", sortOptions = false)
public class UpdateConfigCmd extends CliCommand<AnypointCli> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConfigCmd.class);

    @Override
    protected void execute() throws Exception {
        String username = CliHelper.read("Anypoint Username", parent.getUsername());
        String password = CliHelper.read("Anypoint Password", parent.getPassword(), true);
        if (StringUtils.isEmpty(password)) {
            password = null;
        }
        String defaultOrg = null;
        if (CliHelper.confirm("Do you wish to set a default organization ?", parent.getDefaultOrganization() != null)) {
            defaultOrg = CliHelper.read("Default organization: ", parent.getDefaultOrganization());
        }
        String defaultEnv = null;
        if (defaultOrg != null && CliHelper.confirm("Do you wish to set a default environment ?", parent.getDefaultEnvironment() != null)) {
            defaultEnv = CliHelper.read("Default environment: ", parent.getDefaultEnvironment());
        }
        boolean valid = validate(username, password, defaultOrg, defaultEnv);
        if (CliHelper.confirm("Confirm you wish to update your configuration with those value", valid)) {
            parent.setUsername(username);
            if (StringUtils.isNotEmpty(password)) {
                parent.setPassword(password);
            }
            parent.setDefaultOrganization(defaultOrg);
            parent.setDefaultEnvironment(defaultEnv);
            cli.setSaveConfig(true);
        }
    }

    private boolean validate(String username, String password, String defaultOrg, String defaultEnv) {
        logger.info("Validating config against anypoint platform");
        AnypointClient anypointClient = new AnypointClient(username, password);
        try {
            anypointClient.authenticate(username, password);
            if (defaultOrg != null) {
                try {
                    Organization organization = anypointClient.findOrganization(defaultOrg);
                    if (defaultEnv != null) {
                        try {
                            organization.findEnvironment(defaultEnv);
                        } catch (NotFoundException e) {
                            logger.warn("WARNING: Default environment " + defaultEnv + " not found");
                        }
                    }
                } catch (NotFoundException e) {
                    logger.warn("WARNING: Default organization " + defaultOrg + " not found");
                }
            }
            return true;
        } catch (HttpException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 401) {
                logger.warn("WARNING: Username/Password are unable to login to anypoint");
            } else {
                logger.warn("WARNING: Failed to validate username/password, due to server error response: " + e.getMessage());
            }
            return false;
        }
    }
}
