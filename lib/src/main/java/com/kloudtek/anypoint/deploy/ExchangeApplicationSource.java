package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;

import java.io.File;
import java.io.IOException;

public class ExchangeApplicationSource extends ApplicationSource {
    public static final String PREFIX = "exchange://";
    private final AnypointClient client;
    private String orgId;
    private String groupId;
    private String artifactId;
    private String version;

    ExchangeApplicationSource(AnypointClient client, String url) throws IllegalArgumentException {
        this.client = client;
        if( ! url.startsWith(PREFIX) ) {
            throw new IllegalArgumentException("Invalid exchange url ( must start with exchange:// ): "+url);
        }
        String[] els = url.substring(PREFIX.length()).split(":");
        if( els.length < 3 || els.length > 4 ) {
            throw new IllegalArgumentException("Invalid exchange url: "+url);
        }
        int offset = 0;
        if( els.length == 3 ) {
            orgId = els[1];
            offset = 1;
        } else {
            orgId = els[0];
        }
        groupId = els[1-offset];
        artifactId = els[2-offset];
        version = els[3-offset];
    }

    public ExchangeApplicationSource(AnypointClient client, String orgId, String groupId, String artifactId, String version) {
        this.client = client;
        this.orgId = orgId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getFileName() {
        return artifactId+"-"+version+".zip";
    }

    @Override
    public File getLocalFile() {
        return null;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public APIProvisioningDescriptor getAPIProvisioningDescriptor() throws IOException {
        return null;
    }
}
