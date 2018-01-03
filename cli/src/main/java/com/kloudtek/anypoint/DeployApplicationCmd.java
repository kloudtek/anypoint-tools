package com.kloudtek.anypoint;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.kloudtek.anypoint.provision.InvalidAnypointDescriptorException;
import com.kloudtek.anypoint.provision.TransformList;
import com.kloudtek.anypoint.runtime.Application;
import com.kloudtek.anypoint.runtime.ApplicationDeploymentFailedException;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.util.UserDisplayableException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Parameters(commandDescription = "Deploy Application")
public class DeployApplicationCmd extends AbstractEnvironmentCmd {
    @Parameter(description = "Target", names = {"-t", "--target"}, required = true)
    protected String target;
    @Parameter(description = "Application name", names = {"-n", "--name"}, required = true)
    protected String appName;
    @Parameter(description = "Application file", names = {"-f", "--file"}, required = true)
    protected File appArch;
    @Parameter(description = "Force deployment even if app is unchanged", names = {"--force"})
    protected boolean force = false;
    @Parameter(description = "Wait for application start", names = {"-w", "--waitstarted"})
    private boolean waitAppStarted;
    @Parameter(description = "Provision anypoint", names = {"-p", "--provisionanypoint"})
    private boolean provisionAnypoint;
    @Parameter(description = "Environment suffix (will be appended to API versions and client application names)", names = {"-s", "--envsuffix"})
    private String envSuffix;
    @DynamicParameter(names = "-D", description = "Provisioning parameters")
    private Map<String, String> provisioningParams = new HashMap<>();

    @Override
    protected void execute(AnypointCli cli, Environment environment) throws HttpException, NotFoundException, InvalidAnypointDescriptorException {
        Server server = environment.findServer(target);
        if (appArch.exists()) {
            try {
                if (provisionAnypoint) {
                    cli.print("Provisioning anypoint ... ");
                    TransformList transformList = cli.getClient().provision(server.getParent().getParent(), appArch, provisioningParams, envSuffix);
                    if (transformList != null) {
                        try {
                            appArch = transformList.applyTransforms(appArch, null);
                        } catch (Exception e) {
                            throw new UserDisplayableException("An error occured while applying application ctransformations: " + e.getMessage(), e);
                        }
                    }
                    cli.println("done");
                }
                if (!force) {
                    if (server.checkApplicationExist(appName, appArch, true)) {
                        cli.print("Application already deployed");
                        return;
                    }
                }
                cli.print("Deploying application ... ");
                Application deploy = server.deploy(appName, appArch);
                cli.println("done");
                if (waitAppStarted) {
                    cli.print("Waiting for app to start ... ");
                    deploy.waitDeployed();
                    cli.println("done");
                }
            } catch (IOException e) {
                throw new UserDisplayableException("Error loading application file: " + e.getMessage(), e);
            } catch (ApplicationDeploymentFailedException e) {
                throw new UserDisplayableException("Application deployment failed: " + e.getMessage(), e);
            }
        } else {
            throw new UserDisplayableException("File not found: " + appArch.getPath());
        }
    }
}
