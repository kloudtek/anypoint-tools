package com.kloudtek.anypoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.ktcli.CliCommand;
import com.kloudtek.ktcli.CliHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "anypoint", showDefaultValues = true, notRequiredWithDefault = true, sortOptions = false,
        subcommands = {
                CHDeployApplicationCmd.class, HDeployApplicationCmd.class, GetRegistrationKeyCmd.class,
                UpdateConfigCmd.class, RequestAPIAccessCmd.class, AddServerToGroupCmd.class,
                ProvisionVPCCmd.class})
public class AnypointCli extends CliCommand<CliCommand> {
    @Option(description = "Anypoint username", names = {"-u", "--username"})
    @JsonProperty
    protected String username;
    @Option(description = "Anypoint password", names = {"-pw", "--password"}, defaultValueMask = "*************")
    @JsonProperty
    protected String password;
    @JsonProperty
    protected String defaultOrganization;
    @JsonProperty
    protected String defaultEnvironment;
    protected AnypointClient client;

    public AnypointCli() {
    }

    public synchronized AnypointClient getClient() {
        if (client == null) {
            client = new AnypointClient(username, password);
        }
        return client;
    }

    public String getDefaultOrganization() {
        return defaultOrganization;
    }

    public void setDefaultOrganization(String defaultOrganization) {
        this.defaultOrganization = defaultOrganization;
    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) {
        new CliHelper<AnypointCli>(AnypointCli::new).initAndRun(args);
    }
}
