package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.deploy.HDeploymentRequest;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.util.UserDisplayableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

@Command(name = "hdeploy", description = "Deploy Application to an on premise server", showDefaultValues = true)
public class HDeployApplicationCmd extends AbstractDeployApplicationCmd {
    private static final Logger logger = LoggerFactory.getLogger(HDeployApplicationCmd.class);
    /**
     * Anypoint target name (Server / Server Group / Cluster)
     */
    @Option(description = "Name of target server / server group / cluster", names = {"-t", "--target"})
    private String target;

    @Override
    protected DeploymentResult deploy(Environment environment) throws ProvisioningException, IOException, HttpException {
        Server server;
        try {
            server = environment.findServerByName(target);
        } catch (NotFoundException e) {
            throw new UserDisplayableException("Target "+target+" not found in env "+ environment.getName());
        }
        HDeploymentRequest req = new HDeploymentRequest(server, appName, source, filename, appProperties, apiProvisioningConfig);
        return req.deploy();
    }
}
