package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.*;
import com.kloudtek.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProvisioningServiceImpl implements ProvisioningService {
    @Override
    public TransformList provision(AnypointClient client, Organization org, File file, Map<String, String> provisioningParams, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry entry = zipFile.getEntry("anypoint.json");
        if (entry != null) {
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                ProvisioningDescriptor provisioningDescriptor = new ProvisioningDescriptor(this, zipFile, provisioningParams, envSuffix);
                String json = IOUtils.toString(inputStream);
                try {
                    ProvisioningDescriptor descriptor = client.getJsonHelper().readJson(provisioningDescriptor, json);
                    descriptor.validate();
                    descriptor.provision(org);
                    return descriptor.getTransformList();
                } catch (InvalidJsonException e) {
                    throw new InvalidAnypointDescriptorException(e.getMessage(), e);
                }
            }
        }
        return new TransformList();
    }
}
