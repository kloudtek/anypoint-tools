package com.kloudtek.anypoint;

import com.beust.jcommander.Parameters;
import com.kloudtek.anypoint.cfg.Config;
import com.kloudtek.anypoint.cfg.ConfigProfile;

import java.io.IOException;
import java.util.Scanner;

@Parameters(commandDescription = "Update configuration")
public class UpdateConfigCmd {
    public void execute(Config config) {
        Scanner console = new Scanner(System.in);
        String profileName = read("Profile", console, config.getDefaultProfileName());
        ConfigProfile cp = config.getProfile(profileName);
        cp.setUsername(read("Username", console, cp.getUsername()));
        cp.setPassword(read("Password", console, cp.getPassword(), true));
        if (confirm("Do you wish to set a default organization ?", console, cp.getDefaultOrganization() != null)) {
            cp.setDefaultOrganization(read("Default Organization", console, cp.getDefaultOrganization()));
        } else {
            cp.setDefaultOrganization(null);
        }
        if (confirm("Do you wish to set a default environment ?", console, cp.getDefaultEnvironment() != null)) {
            cp.setDefaultEnvironment(read("Default Environment", console, cp.getDefaultEnvironment()));
        } else {
            cp.setDefaultEnvironment(null);
        }
        config.setDefaultProfileName(read("Default profile", console, profileName));
        boolean valid = validate(cp);
        if (confirm("Confirm you wish to update your configuration with those value", console, valid)) {
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
        AnypointClient anypointClient = new AnypointClient(cp.getUsername(), cp.getPassword());
        try {
            anypointClient.authenticate(cp.getUsername(), cp.getPassword());
            return true;
        } catch (HttpException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 401) {
                System.out.println("WARNING: Username/Password are unable to login to anypoint");
            } else {
                System.out.println("WARNING: Failed to validate username/password, due to server error response: " + e.getMessage());
            }
            return false;
        }
    }

    private String read(String txt, Scanner console, String defVal) {
        return read(txt, console, defVal, false);
    }

    private String read(String txt, Scanner console, String defVal, boolean password) {
        for (; ; ) {
            System.out.print(txt);
            if (defVal != null) {
                System.out.print(" [" + (password ? "********" : defVal) + "]");
            }
            System.out.print(": ");
            String val = console.nextLine();
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

    private boolean confirm(String txt, Scanner console) {
        return confirm(txt, console, null);
    }

    private boolean confirm(String txt, Scanner console, Boolean defaultValue) {
        for (; ; ) {
            String defValStr = null;
            if (defaultValue != null && defaultValue) {
                defValStr = "yes";
            } else if (defaultValue != null && !defaultValue) {
                defValStr = "no";
            }
            String val = read(txt, console, defValStr);
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
