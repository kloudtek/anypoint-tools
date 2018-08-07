package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.runtime.Application;
import com.kloudtek.anypoint.runtime.DeploymentResult;
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
    private final Server target;

    public OnPremDeploymentOperation(Server target) {
        this.target = target;
    }

    @Override
    public DeploymentResult deploy(String appName, String filename, File file) throws IOException, HttpException {
        HttpHelper.MultiPartRequest request;
        long start = System.currentTimeMillis();
        HttpHelper httpHelper = target.getClient().getHttpHelper();
        try {
            logger.debug("Searching for pre-existing application named " + appName);
            Application application = target.findApplication(appName);
            logger.debug("Found application named {} : {}", appName, application.getId());
            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(),
                    target.getParent());
        } catch (NotFoundException e) {
            logger.debug("Couldn't find application named {}", appName);
            request = httpHelper.createMultiPartPostRequest("/hybrid/api/v1/applications", target.getParent());
        }
        String json = request.addText("targetId", target.getId())
                .addText("artifactName", appName)
                .addBinary("file", new StreamSource() {
            @Override
            public String getFileName() {
                return filename;
            }

            @Override
            public InputStream createInputStream() throws IOException {
                return new FileInputStream(file);
            }
        }).execute();
        if (logger.isDebugEnabled()) {
            logger.debug("File upload took " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds");
        }
        Application application = target.getClient().getJsonHelper().readJson(new Application(target), json, "/data");
        return new DeploymentResult(application);
    }
}
