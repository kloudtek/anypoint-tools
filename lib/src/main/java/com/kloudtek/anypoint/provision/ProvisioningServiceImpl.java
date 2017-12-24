package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.*;
import com.kloudtek.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProvisioningServiceImpl extends ProvisioningService {
    @Override
    public void provision(AnypointClient client, Organization org, String appName, File file, Map<String, String> provisioningParams, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry entry = zipFile.getEntry("anypoint.json");
        if (entry != null) {
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                ProvisioningDescriptor provisioningDescriptor = new ProvisioningDescriptor(zipFile, provisioningParams, envSuffix);
                String json = IOUtils.toString(inputStream);
                try {
                    ProvisioningDescriptor descriptor = client.getJsonHelper().readJson(provisioningDescriptor, json);
                    descriptor.validate();
                    descriptor.provision(zipFile, org);
                } catch (InvalidJsonException e) {
                    throw new InvalidAnypointDescriptorException(e.getMessage(), e);
                }
            }
        }
    }
}
