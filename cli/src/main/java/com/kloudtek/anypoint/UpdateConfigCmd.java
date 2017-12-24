package com.kloudtek.anypoint;

import com.beust.jcommander.Parameters;
import com.kloudtek.anypoint.cfg.Config;
import com.kloudtek.anypoint.cfg.ConfigProfile;

import java.io.IOException;

@Parameters(commandDescription = "Update configuration")
public class UpdateConfigCmd {
    private AnypointCli cli;

    public void execute(AnypointCli cli) {
        this.cli = cli;
        Config config = cli.getConfig();
        String profileName = cli.read("Profile", config.getDefaultProfileName());
        ConfigProfile cp = config.getProfile(profileName);
        cp.setUsername(cli.read("Username", cp.getUsername()));
        cp.setPassword(cli.read("Password", cp.getPassword(), true));
        if (cli.confirm("Do you wish to set a default organization ?", cp.getDefaultOrganization() != null)) {
            cp.setDefaultOrganization(cli.read("Default Organization", cp.getDefaultOrganization()));
        } else {
            cp.setDefaultOrganization(null);
        }
        if (cli.confirm("Do you wish to set a default environment ?", cp.getDefaultEnvironment() != null)) {
            cp.setDefaultEnvironment(cli.read("Default Environment", cp.getDefaultEnvironment()));
        } else {
            cp.setDefaultEnvironment(null);
        }
        config.setDefaultProfileName(cli.read("Default profile", profileName));
        boolean valid = validate(cp);
        if (cli.confirm("Confirm you wish to update your configuration with those value", valid)) {
            try {
                config.save();
                System.out.println("Updated configuration file " + config.getFilePath());
            } catch (IOException e) {
                System.err.println("Unable to save configuration: " + e.getMessage());
                System.exit(-1);
            }
        }
    }

    private boolean validate(ConfigProfile cp) {
        System.out.print("Validating config against anypoint platform...");
        AnypointClient anypointClient = new AnypointClient(cp.getUsername(), cp.getPassword());
        try {
            anypointClient.authenticate(cp.getUsername(), cp.getPassword());
            if (cp.getDefaultOrganization() != null) {
                try {
                    Organization organization = anypointClient.findOrganization(cp.getDefaultOrganization());
                    if (cp.getDefaultEnvironment() != null) {
                        organization.findEnvironment(cp.getDefaultEnvironment());
                    }
                } catch (NotFoundException e) {
                    System.out.println("failed\nWARNING: Default organization " + cp.getDefaultOrganization() + " not found");
                }
            }
            System.out.println("successful");
            return true;
        } catch (HttpException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 401) {
                System.out.println("failed\nWARNING: Username/Password are unable to login to anypoint");
            } else {
                System.out.println("failed\nWARNING: Failed to validate username/password, due to server error response: " + e.getMessage());
            }
            return false;
        }
    }
}
