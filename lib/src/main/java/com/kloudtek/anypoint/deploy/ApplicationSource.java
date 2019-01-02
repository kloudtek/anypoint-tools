package com.kloudtek.anypoint.deploy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class ApplicationSource {
    public abstract String getFileName();

    public abstract File getLocalFile();

    public abstract boolean exists();

    public abstract APIProvisioningDescriptor getAPIProvisioningDescriptor() throws IOException;

    public abstract String getArtifactId();

    public static ApplicationSource create(String orgId, AnypointClient client, String path) {
        if (path.startsWith("exchange://")) {
            return new ExchangeApplicationSource(orgId, client,path);
        } else {
            return new FileApplicationSource(client,new File(path));
        }
    }

    public abstract Map<String,Object> getSourceJson(JsonHelper jsonHelper);

//    public abstract HttpHelper.MultiPartRequest addDeploymentJson(HttpHelper.MultiPartRequest multiPartRequest, String filename, Map<String, Object> appInfoMap);
}
