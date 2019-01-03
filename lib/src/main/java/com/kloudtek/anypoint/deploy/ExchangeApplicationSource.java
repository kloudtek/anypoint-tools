package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.util.JsonHelper;
import com.kloudtek.util.TempFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ExchangeApplicationSource extends ApplicationSource {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeApplicationSource.class);
    public static final String PREFIX = "exchange://";
    private APIProvisioningDescriptor apiProvisioningDescriptor;
    private String orgId;
    private String groupId;
    private String artifactId;
    private String version;

    ExchangeApplicationSource(String orgId, AnypointClient client, String url) throws IllegalArgumentException {
        super(client);
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
        super(client);
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
        return artifactId + "-" + version;
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
    public APIProvisioningDescriptor getAPIProvisioningDescriptor() throws IOException, HttpException {
        if (apiProvisioningDescriptor == null) {
            try (TempFile tempFile = new TempFile("anyp-apparch")) {
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    client.getHttpHelper().httpGetBasicAuth("https://maven.anypoint.mulesoft.com/api/v1/organizations/" + orgId + "/maven/" + groupId + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + "-mule-application.jar", fos);
                }
                apiProvisioningDescriptor = readDescriptorFromZip(tempFile);
                return apiProvisioningDescriptor;
            }
        }
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

    @Override
    public void close() throws IOException {
    }
}
