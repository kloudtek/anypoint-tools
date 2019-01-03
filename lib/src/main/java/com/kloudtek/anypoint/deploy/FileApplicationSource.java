package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.util.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileApplicationSource extends ApplicationSource {
    private File file;

    FileApplicationSource(AnypointClient client, File file) {
        super(client);
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
        return readDescriptorFromZip(file);
    }

    @Override
    public String getArtifactId() {
        return file.getName();
    }

    @Override
    public Map<String, Object> getSourceJson(JsonHelper jsonHelper) {
        throw new UnsupportedOperationException("getSourceJson() not supported for file source");
    }

    @Override
    public void close() throws IOException {
    }
}
