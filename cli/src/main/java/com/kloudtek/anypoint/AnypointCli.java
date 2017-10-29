package com.kloudtek.anypoint;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.cfg.Config;
import com.kloudtek.anypoint.cfg.ConfigProfile;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AnypointCli {
    public static final String GETREGKEY = "getregkey";
    public static final String CONFIG = "config";
    @Parameter(description = "Profile to get configuration from", names = {"-p", "--profile"})
    private String profileName = "default";
    @Parameter(description = "Configuration file", names = {"-c", "--config"})
    private String configFile = System.getProperty("user.home") + File.separator + ".anypoint";
    @Parameter(description = "Anypoint username", names = {"-u", "--username"})
    private String username;
    @Parameter(description = "Anypoint password", names = {"-pw", "--password"})
    private String password;
    private Config config;
    private AnypointClient client;
    private final GetRegistrationKeyCmd getRegistrationKeyCmd;
    private final UpdateConfigCmd updateConfigCmd;
    private static final Console console;
    private static Scanner scanner;

    static {
        console = System.console();
        if (console == null) {

        }
        scanner = new Scanner(System.in);
    }

    private ConfigProfile profile;

    public AnypointCli() {
        getRegistrationKeyCmd = new GetRegistrationKeyCmd();
        updateConfigCmd = new UpdateConfigCmd();
    }

    void loadConfig() throws IOException {
        File cfgFile = new File(configFile);
        if (cfgFile.exists()) {
            config = new Config(cfgFile);
            config = new ObjectMapper().readerForUpdating(config).readValue(cfgFile);
        } else {
            config = new Config(cfgFile, new ConfigProfile());
        }
        profile = config.getDefaultProfile();
        if (profile != null) {
            if (username == null) {
                username = profile.getUsername();
            }
            if (password == null) {
                password = profile.getPassword();
            }
        }
    }

    public static void main(String[] args) {
        try {
            AnypointCli cli = new AnypointCli();
            JCommander jc = JCommander.newBuilder().addObject(cli)
                    .addCommand(GETREGKEY, cli.getRegistrationKeyCmd)
                    .addCommand(CONFIG, cli.updateConfigCmd)
                    .build();
            jc.setProgramName("anypoint");
            if (args != null && args.length > 0) {
                jc.parse(args);
            } else {
                jc.usage();
            }
            cli.loadConfig();
            String cmd = jc.getParsedCommand();
            if (CONFIG.equals(cmd)) {
                cli.updateConfig();
            } else if (GETREGKEY.equals(cmd)) {
                cli.getRegistrationKey();
            }
        } catch (Exception e) {
            System.out.print("An error has occurred: ");
            System.out.println(e.getMessage());
        }
    }

    private void getRegistrationKey() throws IOException, NotFoundException, HttpException {
        getRegistrationKeyCmd.execute(this);
    }

    private void updateConfig() {
        updateConfigCmd.execute(this);
    }

    public Config getConfig() {
        return config;
    }

    public String readLine() {
        if (console != null) {
            return console.readLine();
        } else {
            return scanner.nextLine();
        }
    }

    public String readPassword() {
        if (console != null) {
            return new String(console.readPassword());
        } else {
            return scanner.nextLine();
        }
    }

    public ConfigProfile getProfile() {
        return profile;
    }

    public synchronized AnypointClient getClient() {
        if (username == null) {
            throw new IllegalArgumentException("username missing");
        } else if (password == null) {
            throw new IllegalArgumentException("psername missing");
        }
        if (client == null) {
            client = new AnypointClient(username, password);
        }
        return client;
    }

    public String getOrganization(String override) {
        if (override == null) {
            String org = profile.getDefaultOrganization();
            if (org == null) {
                throw new IllegalArgumentException("organization parameter missing");
            } else {
                return org;
            }
        } else {
            return override;
        }
    }

    public String getEnvironment(String override) {
        if (override == null) {
            String envName = profile.getDefaultEnvironment();
            if (envName == null) {
                throw new IllegalArgumentException("environment parameter missing");
            } else {
                return envName;
            }
        } else {
            return override;
        }
    }
}
