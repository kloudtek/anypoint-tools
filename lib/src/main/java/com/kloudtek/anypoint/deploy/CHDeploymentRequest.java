package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.cloudhub.CHMuleVersion;
import com.kloudtek.anypoint.cloudhub.CHWorkerType;
import com.kloudtek.anypoint.runtime.*;
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

import static com.kloudtek.util.StringUtils.isBlank;

public class CHDeploymentRequest extends DeploymentRequest {
    private static final Logger logger = LoggerFactory.getLogger(CHDeploymentRequest.class);
    private int workerCount;
    private CHMuleVersion muleVersion;
    private String region;
    private CHWorkerType workerType;

    public CHDeploymentRequest() {
    }

    public CHDeploymentRequest(String muleVersionName, String regionName, String workerTypeName, int workerCount,
                               Environment environment, String appName, File file, String filename,
                               Map<String,String> properties, APIProvisioningConfig apiProvisioningConfig) throws HttpException, NotFoundException {
        super(environment, appName, file, filename, properties, apiProvisioningConfig);
        this.workerCount = workerCount;
        if (isBlank(muleVersionName)) {
            muleVersion = environment.findDefaultCHMuleVersion();
        } else {
            muleVersion = environment.findCHMuleVersion(muleVersionName);
        }
        if (isBlank(regionName)) {
            region = environment.findDefaultCHRegion().getId();
        } else {
            region = regionName;
        }
        if (isBlank(workerTypeName)) {
            workerType = environment.findSmallestWorkerType();
        } else {
            workerType = environment.findWorkerType(workerTypeName);
        }
    }

    @Override
    protected DeploymentResult doDeploy() throws IOException, HttpException {
        HttpHelper.MultiPartRequest request;
        long start = System.currentTimeMillis();
        AnypointClient client = environment.getClient();
        HttpHelper httpHelper = client.getHttpHelper();
        String appInfoJson;
        try {
            logger.debug("Searching for pre-existing application named " + appName);
            CHApplication application = environment.findCHApplication(appName);
            logger.debug("Found application named {}", appName);
            request = httpHelper.createMultiPartPutRequest("/cloudhub/api/v2/applications/" + application.getDomain(),
                    environment);
            appInfoJson = "{}";
        } catch (NotFoundException e) {
            logger.debug("Couldn't find application named {}", appName);
            request = httpHelper.createMultiPartPostRequest("/cloudhub/api/v2/applications",getEnvironment())
                    .addText("autoStart","true");
            JsonHelper.MapBuilder appInfoBuilder = client.getJsonHelper().buildJsonMap()
                    .set("properties",properties)
                    .set("region",region)
                    .set("domain",appName)
                    .set("monitoringEnabled", true)
                    .set("monitoringAutoRestart", true)
                    .set("fileName", filename);
            appInfoBuilder.addMap("workers")
                    .set("amount",workerCount)
                    .set("type",workerType);
            appInfoJson = new String(client.getJsonHelper().toJson(appInfoBuilder
                    .toMap()));
        }
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
        return new CHDeploymentResult(client.getJsonHelper().readJson(new CHApplication(), json, environment));
    }
}
