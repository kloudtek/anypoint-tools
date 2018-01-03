package com.kloudtek.anypoint;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.cfg.Config;
import com.kloudtek.anypoint.cfg.ConfigProfile;
import com.kloudtek.util.UserDisplayableException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AnypointCli {
    public static final String GETREGKEY = "getregkey";
    public static final String CONFIG = "config";
    public static final String ADDSERVERTOGROUP = "addservertogroup";
    public static final String REQUESTAPIACCESS = "requestapiaccess";
    public static final String DEPLOY = "deploy";
    @Parameter(description = "Profile", names = {"-p", "--profile"})
    private String profileName;
    @Parameter(description = "Configuration file", names = {"-c", "--config"})
    private String configFile = System.getProperty("user.home") + File.separator + ".anypoint.cfg";
    @Parameter(description = "Anypoint username", names = {"-u", "--username"})
    private String username;
    @Parameter(description = "Anypoint password", names = {"-pw", "--password"})
    private String password;
    @Parameter(description = "If true informational messages will be suppressed", names = {"-q","--quiet"})
    private boolean quiet = false;
    @Parameter(description = "Enable verbose logging", names = {"-v", "--verbose"})
    private boolean verbose = false;
    private boolean configExists;
    private Config config;
    private AnypointClient client;
    private ConfigProfile profile;
    private final GetRegistrationKeyCmd getRegistrationKeyCmd = new GetRegistrationKeyCmd();
    private final UpdateConfigCmd updateConfigCmd = new UpdateConfigCmd();
    private final AddServerToGroupCmd addServerToGroupCmd = new AddServerToGroupCmd();
    private final RequestAPIAccessCmd requestAPIAccessCmd = new RequestAPIAccessCmd();
    private final DeployApplicationCmd deployApplicationCmd = new DeployApplicationCmd();
    private static final Console console;
    private static Scanner scanner;

    static {
        console = System.console();
        if (console == null) {
            scanner = new Scanner(System.in);
        }
    }

    private LoggerContext logCtx;

    public AnypointCli() {
    }

    void loadConfig() throws IOException {
        File cfgFile = new File(configFile);
        configExists = cfgFile.exists();
        if (configExists) {
            config = new Config(cfgFile);
            config = new ObjectMapper().readerForUpdating(config).readValue(cfgFile);
        } else {
            config = new Config(cfgFile, new ConfigProfile());
        }
        profile = profileName != null ? config.getProfile(profileName) : config.getDefaultProfile();
        if (profile != null) {
            if (username == null) {
                username = profile.getUsername();
            }
            if (password == null) {
                password = profile.getPassword();
            }
        }
    }

    private void setupLogging() {
        Level lvl;
        if (verbose) {
            lvl = Level.DEBUG;
        } else if (quiet) {
            lvl = Level.WARN;
        } else {
            lvl = Level.INFO;
        }
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("BuilderTest");
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
                .addAttribute("level", lvl));
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target",
                ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%msg%n"));
        builder.add(appenderBuilder);
        builder.add(builder.newLogger("org.apache.logging.log4j", Level.INFO).add(builder.newAppenderRef("Stdout")).addAttribute("additivity", false));
        builder.add(builder.newRootLogger(lvl).add(builder.newAppenderRef("Stdout")));
        logCtx = Configurator.initialize(builder.build());
    }


    public static void main(String[] args) {
        try {
            AnypointCli cli = new AnypointCli();
            JCommander jc = JCommander.newBuilder().addObject(cli)
                    .addCommand(GETREGKEY, cli.getRegistrationKeyCmd)
                    .addCommand(CONFIG, cli.updateConfigCmd)
                    .addCommand(ADDSERVERTOGROUP, cli.addServerToGroupCmd)
                    .addCommand(REQUESTAPIACCESS, cli.requestAPIAccessCmd)
                    .addCommand(DEPLOY, cli.deployApplicationCmd)
                    .build();
            jc.setProgramName("anypoint");
            if (args != null && args.length > 0) {
                jc.parse(args);
            } else {
                jc.usage();
            }
            cli.setupLogging();
            cli.loadConfig();
            String cmd = jc.getParsedCommand();
            if (CONFIG.equals(cmd)) {
                cli.updateConfigCmd.execute(cli);
            } else if (GETREGKEY.equals(cmd)) {
                cli.getRegistrationKeyCmd.execute(cli);
            } else if (ADDSERVERTOGROUP.equals(cmd)) {
                cli.addServerToGroupCmd.execute(cli);
            } else if (REQUESTAPIACCESS.equals(cmd)) {
                cli.requestAPIAccessCmd.execute(cli);
            } else if (DEPLOY.equals(cmd)) {
                cli.deployApplicationCmd.execute(cli);
            }
        } catch (UserDisplayableException | NotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (ParameterException e) {
            System.out.print("Invalid parameters: " + e.getMessage());
            System.exit(-1);
        } catch (HttpException e ) {
            System.out.print("Failed to contact anypoint servers: "+e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.out.print("An error has occurred: ");
            e.printStackTrace();
            System.exit(-1);
        }
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

    public String getOrganizationName(String override) {
        if (override == null) {
            String org = profile.getDefaultOrganization();
            if (org == null) {
                throw new UserDisplayableException("organization parameter missing");
            } else {
                return org;
            }
        } else {
            return override;
        }
    }

    public String getEnvironmentName(String override) {
        if (override == null) {
            String envName = profile.getDefaultEnvironment();
            if (envName == null) {
                throw new UserDisplayableException("environment parameter missing");
            } else {
                return envName;
            }
        } else {
            return override;
        }
    }

    public String read(String txt, String defVal) {
        return read(txt, defVal, false);
    }

    public String read(String txt, String defVal, boolean password) {
        for (; ; ) {
            System.out.print(txt);
            if (defVal != null) {
                System.out.print(" [" + (password ? "********" : defVal) + "]");
            }
            System.out.print(": ");
            System.out.flush();
            String val = password ? readPassword() : readLine();
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

    public boolean confirm(String txt, Boolean defaultValue) {
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

    public void print(String message) {
        if( ! quiet ) {
            System.out.print(message);
        }
    }

    public void println(String message) {
        if( ! quiet ) {
            System.out.println(message);
        }
    }
}
