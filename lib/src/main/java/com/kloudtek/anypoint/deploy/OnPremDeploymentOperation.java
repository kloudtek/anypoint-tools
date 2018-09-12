package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.runtime.HApplication;
import com.kloudtek.anypoint.runtime.HDeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class OnPremDeploymentOperation implements DeploymentOperation {
    private static final Logger logger = LoggerFactory.getLogger(OnPremDeploymentOperation.class);
    private final OnPremDeploymentRequest request;

    public OnPremDeploymentOperation(OnPremDeploymentRequest request) {
        this.request = request;
    }

    @Override
    public HDeploymentResult deploy(Environment environment, String appName, String filename, File file) throws IOException, HttpException {
//        Server target = new Server(null);
//        HttpHelper.MultiPartRequest request;
//        long start = System.currentTimeMillis();
//        HttpHelper httpHelper = target.getClient().getHttpHelper();
//        try {
//            logger.debug("Searching for pre-existing application named " + appName);
//            HApplication application = target.findApplication(appName);
//            logger.debug("Found application named {} : {}", appName, application.getId());
//            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(),
//                    target.getParent());
//        } catch (NotFoundException e) {
//            logger.debug("Couldn't find application named {}", appName);
//            request = httpHelper.createMultiPartPostRequest("/hybrid/api/v1/applications", target.getParent());
//        }
//        String json = request.addText("targetId", target.getId())
//                .addText("artifactName", appName)
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
//        HApplication application = target.getClient().getJsonHelper().readJson(new HApplication(target), json, "/data");
//        return new HDeploymentResult(application);
        return null;
    }
}
