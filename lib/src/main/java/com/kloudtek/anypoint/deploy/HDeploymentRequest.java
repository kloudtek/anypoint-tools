package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.runtime.HApplication;
import com.kloudtek.anypoint.runtime.HDeploymentResult;
import com.kloudtek.anypoint.runtime.Server;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;
import com.kloudtek.anypoint.util.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HDeploymentRequest extends DeploymentRequest {
    private static final Logger logger = LoggerFactory.getLogger(HDeploymentRequest.class);
    private Server target;

    public HDeploymentRequest(Server target, String appName, File file, String filename, Map<String, String> properties, APIProvisioningConfig apiProvisioningConfig) {
        super(target.getParent(), appName, file, filename, properties, apiProvisioningConfig);
        this.target = target;
    }

    @Override
    protected DeploymentResult doDeploy() throws IOException, HttpException {
        HttpHelper.MultiPartRequest request;
        long start = System.currentTimeMillis();
        AnypointClient client = environment.getClient();
        HttpHelper httpHelper = client.getHttpHelper();
        try {
            logger.debug("Searching for pre-existing application named " + appName);
            HApplication application = target.findApplication(appName);
            logger.debug("Found application named {} : {}", appName, application.getId());
            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(),
                    target.getParent());
        } catch (NotFoundException e) {
            logger.debug("Couldn't find application named {}", appName);
            request = httpHelper.createMultiPartPostRequest("/cloudhub/api/v2/applications",environment);
        }
        JsonHelper.MapBuilder appInfoBuilder = client.getJsonHelper().buildJsonMap()
                .set("fileName", filename);

        String appInfoJson = new String(client.getJsonHelper().toJson(appInfoBuilder
                .toMap()));
        String json = request.addText("appInfoJson", appInfoJson)
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
        HApplication application = target.getClient().getJsonHelper().readJson(new HApplication(target), json, "/data");
        return new HDeploymentResult(null);
    }
}
