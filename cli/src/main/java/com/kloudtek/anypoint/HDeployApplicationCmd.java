package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.provision.ProvisioningException;
import com.kloudtek.anypoint.runtime.HDeploymentResult;
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
    protected HDeploymentResult deploy(Environment environment) throws ProvisioningException, IOException {
        return null;
    }
}
