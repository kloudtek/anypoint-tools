package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.Organization;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class ProvisioningService {
    public static ProvisioningService getService() {
        return new ProvisioningServiceImpl();
    }

    public abstract void provision(AnypointClient client, Organization parent, String appName, File file, Map<String, String> provisioningParams, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException;
}
