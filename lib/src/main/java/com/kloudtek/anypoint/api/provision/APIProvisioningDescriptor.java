package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.APISpec;
import com.kloudtek.anypoint.api.ClientApplication;
import com.kloudtek.anypoint.api.policy.Policy;
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
    private List<ProvisionedAPIAccess> access;
    private String name;
    private String version;
    private String endpoint;
    private List<PolicyDescriptor> policies;
    private List<String> accessedBy;
    private Boolean mule4;
    private boolean createClientApplication = true;
    private ClientApplicationDescriptor clientApp;

    public APIProvisioningDescriptor() {
    }

    public APIProvisioningResult provision(Environment environment, APIProvisioningConfig config) throws NotFoundException, HttpException {
        APIProvisioningResult result = new APIProvisioningResult();
        config.setVariable("environment.id", environment.getId());
        config.setVariable("environment.name", environment.getName());
        config.setVariable("environment.lname", environment.getName().toLowerCase());
        config.setVariable("organization.name", environment.getOrganization().getName());
        config.setVariable("organization.lname", environment.getOrganization().getName().toLowerCase());
        logger.debug("Provisioning " + this + " within org " + environment.getParent().getName() + " env " + environment.getName());
        logger.debug("Provisioning " + this.getName());
        String apiName = applyVars(this.getName(), config);
        config.setVariable("api.name", apiName);
        config.setVariable("api.lname", apiName.toLowerCase());
        String apiVersionName = applyVars(this.getVersion(), config);
        if (clientApp == null) {
            clientApp = new ClientApplicationDescriptor();
        }
        String clientAppName = applyVars(clientApp.getName(), config);
        String endpoint = applyVars(this.getEndpoint(), config);
        API api;
        try {
            api = environment.findAPIByExchangeAssetNameAndVersion(apiName, apiVersionName);
            logger.debug("API " + apiName + " " + apiVersionName + " exists: " + api);
        } catch (NotFoundException e) {
            logger.debug("API " + apiName + " " + apiVersionName + " not found, creating");
            APISpec apiSpec = environment.getParent().findAPISpecsByNameAndVersion(this.getName(), this.getVersion());
            Boolean mule4 = this.getMule4();
            if (mule4 == null) {
                mule4 = true;
            }
            api = environment.createAPI(apiSpec, mule4, this.getEndpoint(), config.getApiLabel());
        }
        result.setApi(api);
        List<PolicyDescriptor> polList = new ArrayList<>();
        if (policies != null) {
            polList.addAll(policies);
        }
        for (PolicyDescriptor policyDescriptor : polList) {
            try {
                Policy policy = api.findPolicyByAsset(policyDescriptor.getGroupId(), policyDescriptor.getAssetId(), policyDescriptor.getAssetVersion(), false);
                logger.debug("Policy found: " + policyDescriptor);
                // TODO implement update policy
//                    if( policy.isUpdateRequired(policyDescriptor) ) {
//                        policy.update(policyDescriptor);
//                    }
            } catch (NotFoundException e) {
                logger.debug("Policy not found, creating: " + policyDescriptor);
                api.createPolicy(policyDescriptor);
            }
        }
        if (isCreateClientApplication()) {
            ClientApplication clientApplication;
            try {
                clientApplication = environment.getOrganization().findClientApplication(clientAppName);
                logger.debug("Client application found: " + clientAppName);
            } catch (NotFoundException e) {
                logger.debug("Client application not found, creating: " + clientAppName);
                clientApplication = environment.getOrganization().createClientApplication(clientAppName, clientApp.getUrl(), clientApp.getDescription());
            }
            result.setClientApplication(clientApplication);
        }
        return result;


//                // Create client application
//                ClientApplication clientApplication;
//                String clientAppUrl = this.getClientAppUrl();
//                String clientAppDescription = this.getClientAppDescription();
//                if (clientAppDescription == null) {
//                    clientAppDescription = this.getDescription();
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
//                String credFile = this.getAddCredsToPropertyFile();
//                if (StringUtils.isNotEmpty(credFile)) {
//                    logger.debug("Adding transformer to add credentials to property file: " + credFile);
//                    transformList.add( new SetPropertyTransformer(credFile,this.getCredIdPropertyName(), clientApplication.getClientId()));
//                    transformList.add( new SetPropertyTransformer(credFile,this.getCredSecretPropertyName(), clientApplication.getClientSecret()));
//                }
//                for (ProvisionedAPIAccess access : this.getAccess()) {
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

    private String applyVars(String str, APIProvisioningConfig config) {
        return StringUtils.substituteVariables(str, config.getVariables());
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getMule4() {
        return mule4;
    }

    public void setMule4(Boolean mule4) {
        this.mule4 = mule4;
    }

    @JsonProperty
    public List<ProvisionedAPIAccess> getAccess() {
        return access != null ? access : Collections.emptyList();
    }

    public void setAccess(List<ProvisionedAPIAccess> access) {
        this.access = access;
    }

    @JsonProperty
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @JsonProperty
    public ClientApplicationDescriptor getClientApp() {
        return clientApp;
    }

    public void setClientApp(ClientApplicationDescriptor clientApp) {
        this.clientApp = clientApp;
    }

    @JsonProperty
    public List<PolicyDescriptor> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyDescriptor> policies) {
        this.policies = policies;
    }

    @JsonProperty
    public List<String> getAccessedBy() {
        return accessedBy;
    }

    public void setAccessedBy(List<String> accessedBy) {
        this.accessedBy = accessedBy;
    }

    @JsonProperty(defaultValue = "true")
    public boolean isCreateClientApplication() {
        return createClientApplication;
    }

    public void setCreateClientApplication(boolean createClientApplication) {
        this.createClientApplication = createClientApplication;
    }
}
