package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.*;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.APIVersion;
import com.kloudtek.anypoint.api.ClientApplication;
import com.kloudtek.anypoint.api.policy.Policy;
import com.kloudtek.anypoint.transformer.SetPropertyTransformer;
import com.kloudtek.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

public class ProvisioningDescriptor {
    private static final Logger logger = LoggerFactory.getLogger(ProvisioningDescriptor.class);
    private ProvisioningServiceImpl provisioningService;
    private ZipFile zipFile;
    private ProvisioningConfig provisioningConfig;
    private String envSuffix;
    private List<ProvisionedAPI> apis;
    private TransformList transformList = new TransformList();

    public ProvisioningDescriptor(ProvisioningServiceImpl provisioningService, ZipFile zipFile, ProvisioningConfig provisioningConfig,
                                  String envSuffix) {
        this.provisioningService = provisioningService;
        this.zipFile = zipFile;
        this.provisioningConfig = provisioningConfig;
        this.envSuffix = envSuffix;
    }

    public TransformList getTransformList() {
        return transformList;
    }

    public void provision(Organization org, ProvisioningConfig provisioningConfig) throws NotFoundException, HttpException, IOException {
        logger.debug("Provisioning " + this + " within org " + org);
        try {
            for (ProvisionedAPI provisionedAPI : apis) {
                String apiName = parseEL(provisionedAPI.getName());
                String apiVersionName = parseEL(provisionedAPI.getVersion());
                String clientAppName = apiName;
                if (!StringUtils.isEmpty(envSuffix)) {
                    apiVersionName = apiVersionName + "-" + envSuffix;
                    clientAppName = clientAppName + "-" + envSuffix;
                }
                String endpoint = parseEL(provisionedAPI.getEndpoint());
                String description = parseEL(provisionedAPI.getDescription());
                // Create API & Version
                API api;
                APIVersion version;
                try {
                    api = org.getAPI(apiName);
                    try {
                        version = api.getVersion(apiVersionName, true);
                        logger.debug("found version " + apiVersionName);
                    } catch (NotFoundException e) {
                        logger.debug("Couldn't find version " + apiVersionName + " creating");
                        version = api.createVersion(apiVersionName, endpoint, description);
                    }
                } catch (NotFoundException e) {
                    logger.debug("Couldn't find api " + apiName + " creating");
                    api = org.createAPI(apiName, apiVersionName, endpoint, description);
                    version = api.getVersion(apiVersionName);
                }

                if (endpoint == null) {
                    endpoint = "http://endpoint";
                }
                if (version.getEndpoint() == null || !endpoint.equals(version.getEndpoint().getUri())) {
                    version.updateEndpoint(endpoint);
                }

                // Setup portal
                if (provisionedAPI.isSetupPortal() && version.getPortalId() == null) {
                    version.createPortal(parseEL(provisionedAPI.getName()));
                }

                // Create policies
                HashMap<String, Policy> policies = version.getPoliciesAsMap(false);
                for (PolicyDescriptor policyDescriptor : provisionedAPI.getPolicies()) {
                    Policy existing = policies.get(policyDescriptor.getType());
                    if (existing == null) {
                        version.createPolicy(policyDescriptor.toPolicy(version));
                    } else if (policyDescriptor.update(existing)) {
                        Policy policy = policyDescriptor.toPolicy(version);
                        policy.setId(existing.getId());
                        version.updatePolicy(policy);
                    }
                }
                // Create client application
                ClientApplication clientApplication;
                String clientAppUrl = provisionedAPI.getClientAppUrl();
                String clientAppDescription = provisionedAPI.getClientAppDescription();
                if (clientAppDescription == null) {
                    clientAppDescription = provisionedAPI.getDescription();
                }
                try {
                    logger.debug("Searching for existing client application {}", clientAppName);
                    clientApplication = org.findClientApplication(clientAppName);
                    logger.debug("Found existing client application {}: {}", clientAppName, clientApplication.getId());
                    // TODO: update clientAppUrl & clientAppDescription
                } catch (NotFoundException e) {
                    logger.debug("Couldn't find existing client application {}, creating", clientAppName);
                    clientApplication = org.createClientApplication(clientAppName, clientAppUrl, clientAppDescription);
                }
                String credFile = provisionedAPI.getAddCredsToPropertyFile();
                if (StringUtils.isNotEmpty(credFile)) {
                    logger.debug("Adding transformer to add credentials to property file: " + credFile);
                    transformList.add( new SetPropertyTransformer(credFile,provisionedAPI.getCredIdPropertyName(), clientApplication.getClientId()));
                    transformList.add( new SetPropertyTransformer(credFile,provisionedAPI.getCredSecretPropertyName(), clientApplication.getClientSecret()));
                }
                for (ProvisionedAPIAccess access : provisionedAPI.getAccess()) {
                    org.requestAPIAccess(clientApplication, parseEL(access.getName()), parseEL(access.getVersion()), true, true, null);
                }
                for (String name : provisioningConfig.getAccessedBy()) {
                    org.requestAPIAccess(name,apiName,apiVersionName,true,true,null);
                }
            }
        } catch (ClassCastException e) {
            throw new IOException("Invalid anypoint.json descriptor", e);
        }
    }

    @JsonProperty
    public List<ProvisionedAPI> getApis() {
        return apis != null ? apis : Collections.emptyList();
    }

    public void setApis(List<ProvisionedAPI> apis) {
        this.apis = apis;
    }

    private String parseEL(String str) {
        return AnypointClient.parseEL(str, provisioningConfig.getProvisioningParams());
    }

    public void validate() {
        if (apis != null) {
            for (ProvisionedAPI api : apis) {
                try {
                    api.validate();
                } catch (InvalidAnypointDescriptorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ProvisioningDescriptor{" +
                "provisioningService=" + provisioningService +
                ", zipFile=" + zipFile +
                ", provisioningConfig=" + provisioningConfig +
                ", envSuffix='" + envSuffix + '\'' +
                ", apis=" + apis +
                ", transformList=" + transformList +
                '}';
    }
}
