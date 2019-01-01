package com.kloudtek.anypoint;

import com.kloudtek.anypoint.provisioning.VPCProvisioningDescriptor;
import com.kloudtek.util.FileUtils;
import com.kloudtek.util.UserDisplayableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;

@Command(name = "provisionvpc", description = "Provision VPC", sortOptions = false)
public class ProvisionVPCCmd extends AbstractOrganizationalCmd {
    private static final Logger logger = LoggerFactory.getLogger(ProvisionVPCCmd.class);
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    private boolean usageHelpRequested;
    @Option(names = {"-d", "--delete"}, description = "Delete pre-existing VPC with same name (and all applications in associated environments) if it exists prior to creation")
    private boolean delete;
    @Option(description = "VPC descriptor file", names = {"-f", "--file"})
    private File file;

    @Override
    protected void execute(Organization organization) throws IOException, NotFoundException, HttpException {
        logger.info("Provisioning VPC");
        if( ! file.exists() ) {
            throw new UserDisplayableException("File doesn't exist: "+file.getPath());
        }
        organization.provisionVPC(file, delete);
        logger.info("VPC Provisioning complete");
    }
}
