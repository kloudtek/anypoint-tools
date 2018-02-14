package com.kloudtek.anypoint;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "getregkey", showDefaultValues = true, sortOptions = false,
        description = "Retrieve a server registration key (requires organization and environment to be specified)")
public class GetRegistrationKeyCmd extends AbstractEnvironmentCmd {
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    boolean usageHelpRequested;

    @Override
    protected void execute(Environment environment) throws Exception {
        String key = environment.getServerRegistrationKey();
        System.out.println(key);
    }
}
