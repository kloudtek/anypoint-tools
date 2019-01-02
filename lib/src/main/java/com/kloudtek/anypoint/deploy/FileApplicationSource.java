package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.util.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
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

    @Override
    public Map<String, Object> getSourceJson(JsonHelper jsonHelper) {
        throw new UnsupportedOperationException("getSourceJson() not supported for file source");
    }
//
//    @Override
//    public HttpHelper.MultiPartRequest addDeploymentJson(HttpHelper.MultiPartRequest multiPartRequest, String filename, Map<String, Object> appInfoMap) {
//        HttpHelper.MultiPartRequest req = multiPartRequest.addBinary("file", new StreamSource() {
//            @Override
//            public String getFileName() {
//                return filename;
//            }
//
//            @Override
//            public InputStream createInputStream() throws IOException {
//                return new FileInputStream(FileApplicationSource.this.file);
//            }
//        });
//        req = req.addText("autoStart", "true");
//        if( appInfoMap != null ) {
//            String appInfoJson = new String(client.getJsonHelper().toJson(appInfoMap));
//            req = req.addText("appInfoJson", appInfoJson);
//        }
//        return req;
//    }
}
