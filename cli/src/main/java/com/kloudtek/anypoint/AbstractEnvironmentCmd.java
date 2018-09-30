package com.kloudtek.anypoint;

import com.kloudtek.util.UserDisplayableException;
import picocli.CommandLine.Option;

public abstract class AbstractEnvironmentCmd extends AbstractOrganizationalCmd {
    @Option(description = "Environment", names = {"-e", "--environment"})
    protected String environmentName;
    @Option(description = "Create environment if it doesn't exist (type will be set to sandbox)", names = {"-ce", "--create-environment"})
    protected boolean createEnvironment = false;
    @Option(description = "Environment type if creation required", names = {"-et", "--env-type"})
    protected Environment.Type createEnvironmentType = Environment.Type.SANDBOX;

    @Override
    protected final void execute(Organization organization) throws Exception {
        if (environmentName == null) {
            environmentName = parent.getDefaultEnvironment();
            if (environmentName == null) {
                throw new NotFoundException("Environment parameter missing");
            }
        } else if (cli.isSaveConfig()) {
            parent.setDefaultEnvironment(environmentName);
        }
        Environment environment = null;
        try {
            environment = organization.findEnvironmentByName(environmentName);
        } catch (NotFoundException e) {
            if (createEnvironment) {
                organization.createEnvironment(environmentName, createEnvironmentType);
            } else {
                throw new UserDisplayableException("Environment not found: " + environmentName);
            }
        }
        execute(environment);
    }

    protected abstract void execute(Environment environment) throws Exception;
}
