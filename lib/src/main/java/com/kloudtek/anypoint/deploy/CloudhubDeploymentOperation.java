package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.runtime.HDeploymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class CloudhubDeploymentOperation implements DeploymentOperation {
    private static final Logger logger = LoggerFactory.getLogger(CloudhubDeploymentOperation.class);

    public CloudhubDeploymentOperation() {
    }

    @Override
    public HDeploymentResult deploy(Environment environment, String appName, String filename, File file) {
//        HttpHelper.MultiPartRequest request;
//        long start = System.currentTimeMillis();
//        AnypointClient client = environment.getClient();
//        HttpHelper httpHelper = client.getHttpHelper();
////        try {
////            logger.debug("Searching for pre-existing application named " + appName);
////            Application application = target.findApplication(appName);
////            logger.debug("Found application named {} : {}", appName, application.getId());
////            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(),
////                    target.getParent());
////        } catch (NotFoundException e) {
//            logger.debug("Couldn't find application named {}", appName);
//            request = httpHelper.createMultiPartPostRequest("/cloudhub/api/v2/applications",environment);
////        }
//        JsonHelper.MapBuilder appInfoBuilder = client.getJsonHelper().buildJsonMap()
//                .set("fileName", filename);
//
//        String appInfoJson = new String(client.getJsonHelper().toJson(appInfoBuilder
//                .toMap()));
//        String json = request.addText("appInfoJson", appInfoJson)
//                .addBinary("file", new StreamSource() {
//            @Override
//            public String getFileName() {
//                return filename;
//            }
//
//            @Override
//            public InputStream createInputStream() throws IOException {
//                return new FileInputStream(file);
//            }
//        }).execute();
//        if (logger.isDebugEnabled()) {
//            logger.debug("File upload took " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds");
//        }
//        Application application = target.getClient().getJsonHelper().readJson(new Application(target), json, "/data");
//        return new HDeploymentResult(null);
        return null;
    }
}
