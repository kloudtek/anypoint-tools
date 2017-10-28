package com.kloudtek.anypoint;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.cfg.Config;
import com.kloudtek.anypoint.cfg.ConfigProfile;

import java.io.File;
import java.io.IOException;

public class AnypointCli {
    public static final String GETREGKEY = "getregkey";
    public static final String CONFIG = "config";
    @Parameter(description = "Profile to get configuration from", names = {"-p", "--profile"})
    private String profile = "default";
    @Parameter(description = "Configuration file", names = {"-c", "--config"})
    private String configFile = System.getProperty("user.home") + File.separator + ".anypoint";
    @Parameter(description = "Anypoint username", names = {"-u", "--username"})
    private String username;
    @Parameter(description = "Anypoint password", names = {"-pw", "--password"})
    private String password;
    private Config config;

    public AnypointCli() {
    }

    void loadProfile() throws IOException {
        File cfgFile = new File(configFile);
        if (cfgFile.exists()) {
            config = new Config(cfgFile);
            config = new ObjectMapper().readerForUpdating(config).readValue(cfgFile);
        } else {
            config = new Config(cfgFile, new ConfigProfile());
        }
    }

    public static void main(String[] args) throws IOException {
        AnypointCli cli = new AnypointCli();
        GetRegistrationKeyCmd getRegistrationKeyCmd = new GetRegistrationKeyCmd();
        UpdateConfigCmd updateConfigCmd = new UpdateConfigCmd();
        JCommander jc = JCommander.newBuilder().addObject(cli)
                .addCommand(GETREGKEY, getRegistrationKeyCmd)
                .addCommand(CONFIG, updateConfigCmd)
                .build();
        jc.setProgramName("anypoint");
        if (args != null && args.length > 0) {
            jc.parse(args);
        } else {
            jc.usage();
        }
        cli.loadProfile();
        if (jc.getParsedCommand().equals(CONFIG)) {
            updateConfigCmd.execute(cli.config);
        }
    }
}
