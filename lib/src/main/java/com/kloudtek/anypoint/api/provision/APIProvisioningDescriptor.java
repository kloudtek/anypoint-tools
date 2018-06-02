package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kloudtek.util.StringUtils.isNotEmpty;

public class APIProvisioningDescriptor {
    private static final Logger logger = LoggerFactory.getLogger(APIProvisioningDescriptor.class);
    private List<ProvisionedAPI> apis = new ArrayList<>();
    private APIProvisioningConfig config;

    public APIProvisioningDescriptor() {
    }

    public void provision(Environment environment, APIProvisioningConfig config) throws NotFoundException, HttpException, IOException {
        this.config = config;
        logger.debug("Provisioning " + this + " within org " + environment.getParent().getName() + " env "+environment.getName());
        for (ProvisionedAPI provisionedAPI : apis) {
            logger.debug("Provisioning "+provisionedAPI.getName());
            String apiName = applyVars(provisionedAPI.getName());
            String apiVersionName = applyVars(provisionedAPI.getVersion());
            String clientAppName = isNotEmpty(provisionedAPI.getClientAppName()) ? applyVars(provisionedAPI.getClientAppName()) : apiName;
            String endpoint = applyVars(provisionedAPI.getEndpoint());
            try {
                API api = environment.findAPIByExchangeAssetName(apiName, apiVersionName);
                System.out.println();
            } catch (NotFoundException e) {
                logger.debug("API "+apiName+" "+apiVersionName+" not found, creating");
                System.out.println();
            }
        }


//        try {
//            for (ProvisionedAPI provisionedAPI : apis) {
//                // Create API & Version
//                APIAsset api;
//                API version;
//                try {
//                    api = org.getAPI(apiName);
//                    try {
//                        version = api.getVersion(apiVersionName, true);
//                        logger.debug("found version " + apiVersionName);
//                    } catch (NotFoundException e) {
//                        logger.debug("Couldn't find version " + apiVersionName + " creating");
//                        version = api.createVersion(apiVersionName, endpoint, description);
//                    }
//                } catch (NotFoundException e) {
//                    logger.debug("Couldn't find api " + apiName + " creating");
//                    api = org.createAPI(apiName, apiVersionName, endpoint, description);
//                    version = api.getVersion(apiVersionName);
//                }
//
//                if (endpoint == null) {
//                    endpoint = "http://endpoint";
//                }
//                if (version.getEndpoint() == null || !endpoint.equals(version.getEndpoint().getUri())) {
//                    version.updateEndpoint(endpoint);
//                }
//                // Setup portal
//                if (provisionedAPI.isSetupPortal() && version.getPortalId() == null) {
//                    version.createPortal(applyVars(provisionedAPI.getName()));
//                }
//                // Create policies
//                HashMap<String, Policy> policies = version.getPoliciesAsMap(false);
//                for (PolicyDescriptor policyDescriptor : provisionedAPI.getPolicies()) {
//                    Policy existing = policies.get(policyDescriptor.getType());
//                    if (existing == null) {
//                        version.createPolicy(policyDescriptor.toPolicy(version));
//                    } else if (policyDescriptor.update(existing)) {
//                        Policy policy = policyDescriptor.toPolicy(version);
//                        policy.setId(existing.getId());
//                        version.updatePolicy(policy);
//                    }
//                }
//                // Create client application
//                ClientApplication clientApplication;
//                String clientAppUrl = provisionedAPI.getClientAppUrl();
//                String clientAppDescription = provisionedAPI.getClientAppDescription();
//                if (clientAppDescription == null) {
//                    clientAppDescription = provisionedAPI.getDescription();
//                }
//                try {
//                    logger.debug("Searching for existing client application {}", clientAppName);
//                    clientApplication = org.findClientApplication(clientAppName);
//                    logger.debug("Found existing client application {}: {}", clientAppName, clientApplication.getId());
//                    // TODO: update clientAppUrl & clientAppDescription
//                } catch (NotFoundException e) {
//                    logger.debug("Couldn't find existing client application {}, creating", clientAppName);
//                    clientApplication = org.createClientApplication(clientAppName, clientAppUrl, clientAppDescription);
//                }
//                String credFile = provisionedAPI.getAddCredsToPropertyFile();
//                if (StringUtils.isNotEmpty(credFile)) {
//                    logger.debug("Adding transformer to add credentials to property file: " + credFile);
//                    transformList.add( new SetPropertyTransformer(credFile,provisionedAPI.getCredIdPropertyName(), clientApplication.getClientId()));
//                    transformList.add( new SetPropertyTransformer(credFile,provisionedAPI.getCredSecretPropertyName(), clientApplication.getClientSecret()));
//                }
//                for (ProvisionedAPIAccess access : provisionedAPI.getAccess()) {
//                    String accessVersion = access.getVersion();
//                    if( envSuffix != null ) {
//                        accessVersion = accessVersion +"-"+envSuffix;
//                    }
//                    org.requestAPIAccess(clientApplication, applyVars(access.getName()), applyVars(accessVersion), true, true, null);
//                }
//                for (String name : provisioningConfig.getAccessedBy()) {
//                    org.requestAPIAccess(applyVars(name),applyVars(apiName),applyVars(apiVersionName),true,true,null);
//                }
//            }
//        } catch (ClassCastException e) {
//            throw new IOException("Invalid anypoint.json descriptor", e);
//        }
    }

    private String applyVars(String str) {
        return StringUtils.substituteVariables(str,config.getVariables());
    }

    @JsonProperty
    public List<ProvisionedAPI> getApis() {
        return apis != null ? apis : Collections.emptyList();
    }

    public void setApis(List<ProvisionedAPI> apis) {
        this.apis = apis;
    }
}
