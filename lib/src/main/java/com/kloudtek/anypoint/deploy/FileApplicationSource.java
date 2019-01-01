package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileApplicationSource extends ApplicationSource {
    private AnypointClient client;
    private File file;

    FileApplicationSource(AnypointClient client, File file) {
        this.client = client;
        this.file = file;
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public File getLocalFile() {
        return file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public APIProvisioningDescriptor getAPIProvisioningDescriptor() throws IOException  {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry anypointJson = zipFile.getEntry("anypoint.json");
        if (anypointJson != null) {
            try (InputStream is = zipFile.getInputStream(anypointJson)) {
                return client.getJsonHelper().getJsonMapper().readValue(is, APIProvisioningDescriptor.class);
            }
        } else {
            return null;
        }
    }

    @Override
    public String getArtifactId() {
        return file.getName();
    }
}
