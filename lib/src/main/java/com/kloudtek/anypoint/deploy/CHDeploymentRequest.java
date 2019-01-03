package com.kloudtek.anypoint.deploy;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningResult;
import com.kloudtek.anypoint.cloudhub.CHMuleVersion;
import com.kloudtek.anypoint.cloudhub.CHWorkerType;
import com.kloudtek.anypoint.runtime.CHApplication;
import com.kloudtek.anypoint.runtime.CHDeploymentResult;
import com.kloudtek.anypoint.runtime.DeploymentResult;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;
import com.kloudtek.anypoint.util.StreamSource;
import com.kloudtek.unpack.transformer.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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

    @Override
    protected void preDeploy(APIProvisioningResult result, APIProvisioningConfig config, List<Transformer> transformers) {
    }

    public CHDeploymentRequest(String muleVersionName, String regionName, String workerTypeName, int workerCount,
                               Environment environment, String appName, ApplicationSource file, String filename,
                               Map<String, String> properties, APIProvisioningConfig apiProvisioningConfig) throws HttpException, NotFoundException {
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
            workerType = environment.findWorkerTypeByName(workerTypeName);
        }
    }

    @Override
    protected DeploymentResult doDeploy() throws IOException, HttpException {
        long start = System.currentTimeMillis();
        AnypointClient client = environment.getClient();
        HttpHelper httpHelper = client.getHttpHelper();
        JsonHelper.MapBuilder appInfoBuilder = client.getJsonHelper().buildJsonMap();
        CHApplication existingApp = getExistingApp(appName);
        appInfoBuilder.set("properties", properties)
                .set("domain", appName)
                .set("monitoringEnabled", true)
                .set("monitoringAutoRestart", true)
                .set("loggingNgEnabled", true)
                .set("loggingCustomLog4JEnabled", apiProvisioningConfig.isCustomLog4j());
        appInfoBuilder.addMap("muleVersion").set("version", muleVersion.getVersion()).set("updateId", muleVersion.getLatestUpdate().getId());
        appInfoBuilder.addMap("workers")
                .set("amount", workerCount)
                .addMap("type")
                .set("name", workerType.getName())
                .set("weight", workerType.getWeight())
                .set("cpu", workerType.getCpu())
                .set("memory", workerType.getMemory());
        appInfoBuilder.set("fileName", filename);
        Map<String, Object> appInfo = appInfoBuilder.toMap();
        String deploymentJson;
        if (source.getLocalFile() != null) {
            HttpHelper.MultiPartRequest req;
            if (existingApp != null) {
                req = httpHelper.createMultiPartPutRequest("/cloudhub/api/v2/applications/" + existingApp.getDomain(),
                        environment);
            } else {
                req = httpHelper.createMultiPartPostRequest("/cloudhub/api/v2/applications", getEnvironment());
                req = req.addText("autoStart", "true");
            }
            String appInfoJson = new String(environment.getClient().getJsonHelper().toJson(appInfo));
            req = req.addText("appInfoJson", appInfoJson);
            req = req.addBinary("file", new StreamSource() {
                @Override
                public String getFileName() {
                    return filename;
                }

                @Override
                public InputStream createInputStream() throws IOException {
                    return new FileInputStream(source.getLocalFile());
                }
            });
            deploymentJson = req.execute();
        } else {
            Map<String, Object> deployJson = new HashMap<>();
            deployJson.put("applicationInfo", appInfo);
            deployJson.put("applicationSource", source.getSourceJson(client.getJsonHelper()));
            if (existingApp != null) {
                deploymentJson = httpHelper.httpPut("/cloudhub/api/v2/applications/" + existingApp.getDomain(), deployJson, environment);
            } else {
                deployJson.put("autoStart", true);
                deploymentJson = httpHelper.httpPost("/cloudhub/api/v2/applications/", deployJson, environment);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("File upload took " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds");
        }
        return new CHDeploymentResult(client.getJsonHelper().readJson(new CHApplication(), deploymentJson, environment));
    }

    private CHApplication getExistingApp(String appName) throws HttpException {
        try {
            logger.debug("Searching for pre-existing application named " + appName);
            CHApplication application = environment.findCHApplicationByDomain(appName);
            logger.debug("Found application named {}", appName);
            return application;
        } catch (NotFoundException e) {
            logger.debug("Couldn't find application named {}", appName);
            return null;
        }
    }

}
