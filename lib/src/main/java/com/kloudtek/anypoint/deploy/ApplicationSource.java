package com.kloudtek.anypoint.deploy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;

import java.io.File;
import java.io.IOException;

public abstract class ApplicationSource {
    public abstract String getFileName();

    public abstract File getLocalFile();

    public abstract boolean exists();

    public abstract APIProvisioningDescriptor getAPIProvisioningDescriptor() throws IOException;

    public abstract String getArtifactId();

    public static ApplicationSource create(AnypointClient client, String path) {
        if (path.startsWith("exchange://")) {
            return new ExchangeApplicationSource(client,path);
        } else {
            return new FileApplicationSource(client,new File(path));
        }
    }
}
