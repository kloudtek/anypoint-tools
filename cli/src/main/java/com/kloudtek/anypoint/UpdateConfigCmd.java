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
        String profileName = read("Profile", config.getDefaultProfileName());
        ConfigProfile cp = config.getProfile(profileName);
        cp.setUsername(read("Username", cp.getUsername()));
        cp.setPassword(read("Password", cp.getPassword(), true));
        if (confirm("Do you wish to set a default organization ?", cp.getDefaultOrganization() != null)) {
            cp.setDefaultOrganization(read("Default Organization", cp.getDefaultOrganization()));
        } else {
            cp.setDefaultOrganization(null);
        }
        if (confirm("Do you wish to set a default environment ?", cp.getDefaultEnvironment() != null)) {
            cp.setDefaultEnvironment(read("Default Environment", cp.getDefaultEnvironment()));
        } else {
            cp.setDefaultEnvironment(null);
        }
        config.setDefaultProfileName(read("Default profile", profileName));
        boolean valid = validate(cp);
        if (confirm("Confirm you wish to update your configuration with those value", valid)) {
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
                        try {
                            organization.findEnvironment(cp.getDefaultEnvironment());
                        } catch (IOException e) {
                            System.out.println("failed\nWARNING: Default environment " + cp.getDefaultEnvironment() + " not found");
                        }
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

    private String read(String txt, String defVal) {
        return read(txt, defVal, false);
    }

    private String read(String txt, String defVal, boolean password) {
        for (; ; ) {
            System.out.print(txt);
            if (defVal != null) {
                System.out.print(" [" + (password ? "********" : defVal) + "]");
            }
            System.out.print(": ");
            System.out.flush();
            String val = password ? cli.readPassword() : cli.readLine();
            if (val != null) {
                val = val.trim();
                if (!val.isEmpty()) {
                    return val;
                }
                if (defVal != null) {
                    return defVal;
                }
            }
        }
    }

    private boolean confirm(String txt) {
        return confirm(txt, null);
    }

    private boolean confirm(String txt, Boolean defaultValue) {
        for (; ; ) {
            String defValStr = null;
            if (defaultValue != null && defaultValue) {
                defValStr = "yes";
            } else if (defaultValue != null && !defaultValue) {
                defValStr = "no";
            }
            String val = read(txt, defValStr);
            if (val != null) {
                val = val.trim().toLowerCase();
                switch (val) {
                    case "yes":
                    case "y":
                    case "true":
                        return true;
                    case "no":
                    case "n":
                    case "false":
                        return false;
                    default:
                        System.out.println("Response must be either: yes, no, n, y, true, false");
                }
            }
        }
    }
}
