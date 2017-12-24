package com.kloudtek.anypoint;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Retrieve a server registration key")
public class GetRegistrationKeyCmd extends AbstractEnvironmentCmd {
    @Override
    protected void execute(AnypointCli cli, Environment environment) throws HttpException {
        String key = environment.getServerRegistrationKey();
        System.out.println(key);
    }
}
