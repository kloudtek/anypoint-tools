package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.util.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ExchangeApplicationSource extends ApplicationSource {
    public static final String PREFIX = "exchange://";
    private final AnypointClient client;
    private String orgId;
    private String groupId;
    private String artifactId;
    private String version;

    ExchangeApplicationSource(String orgId, AnypointClient client, String url) throws IllegalArgumentException {
        this.client = client;
        if( ! url.startsWith(PREFIX) ) {
            throw new IllegalArgumentException("Invalid exchange url ( must start with exchange:// ): "+url);
        }
        String[] els = url.substring(PREFIX.length()).split(":");
        if( els.length < 2 || els.length > 4 ) {
            throw new IllegalArgumentException("Invalid exchange url: "+url);
        }
        if( els.length == 2 ) {
            this.orgId = orgId;
            this.groupId = this.orgId;
            artifactId = els[0];
            version = els[1];
        } else if( els.length == 3 ) {
            this.orgId = els[0];
            groupId = this.orgId;
            artifactId = els[1];
            version = els[2];
        } else {
            this.orgId = els[0];
            groupId = els[1];
            artifactId = els[2];
            version = els[3];
        }
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

    @Override
    public Map<String, Object> getSourceJson(JsonHelper jsonHelper) {
        return jsonHelper.buildJsonMap()
                .set("source","EXCHANGE")
                .set("groupId",groupId)
                .set("artifactId",artifactId)
                .set("version",version)
                .set("organizationId",orgId)
                .toMap();
    }
}
