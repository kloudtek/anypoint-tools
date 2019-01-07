package com.kloudtek.anypoint;

import com.kloudtek.anypoint.runtime.manifest.ReleaseManifestDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.URI;

@Command(name = "updaterelmanifest", description = "Provision VPC", sortOptions = false)
public class UpdateReleaseManifestCmd extends AbstractOrganizationalCmd {
    private static final Logger logger = LoggerFactory.getLogger(UpdateReleaseManifestCmd.class);
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    private boolean usageHelpRequested;
    @CommandLine.Parameters(description = "Manifest URI", index = "0")
    private URI manifestUri;
    @CommandLine.Parameters(description = "Artifact id", index = "1")
    private String artifactId;
    @CommandLine.Parameters(description = "Artifact version", index = "2")
    private String version;
    @Option(names = {"-g", "--groupid"}, description = "Artifact group id")
    private String groupId;

    @Override
    protected void execute(Organization organization) throws IOException, NotFoundException, HttpException {
        ReleaseManifestDAO manifest = ReleaseManifestDAO.load(organization, manifestUri);
        manifest.update(groupId, artifactId, version);
    }
}
