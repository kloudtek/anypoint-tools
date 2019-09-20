package com.kloudtek.anypoint.api.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.api.*;
import com.kloudtek.anypoint.api.policy.Policy;
import com.kloudtek.anypoint.exchange.AssetInstance;
import com.kloudtek.anypoint.exchange.ExchangeAsset;
import com.kloudtek.util.InvalidStateException;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.validation.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class APIProvisioningDescriptor {
    private static final Logger logger = LoggerFactory.getLogger(APIProvisioningDescriptor.class);
    private List<APIAccessDescriptor> access;
    private String name;
    private String version;
    private String endpoint;
    private List<PolicyDescriptor> policies;
    private List<String> accessedBy;
    private Boolean mule4;
    private boolean createClientApplication = true;
    private ClientApplicationDescriptor clientApp;
    private List<SLATierDescriptor> slaTiers;

    public APIProvisioningDescriptor() {
    }

    public APIProvisioningDescriptor(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public APIProvisioningResult provision(Environment environment, APIProvisioningConfig config) throws ProvisioningException {
        try {
            APIProvisioningResult result = new APIProvisioningResult();
            config.setVariable("environment.id", environment.getId());
            config.setVariable("environment.name", environment.getName());
            config.setVariable("environment.lname", environment.getName().toLowerCase());
            config.setVariable("organization.name", environment.getOrganization().getName());
            config.setVariable("organization.lname", environment.getOrganization().getName().toLowerCase());
            ValidationUtils.notEmpty(IllegalStateException.class, "API Descriptor missing value: name", name);
            ValidationUtils.notEmpty(IllegalStateException.class, "API Descriptor missing value: version", version);
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
                    Policy policy = api.findPolicyByAsset(policyDescriptor.getGroupId(), policyDescriptor.getAssetId(), policyDescriptor.getAssetVersion());
                    if (Objects.deepEquals(policy.getConfigurationData(), policyDescriptor.getData()) && Objects.deepEquals(policy.getPointcutData(), policyDescriptor.getPointcutData())) {
                        logger.debug("Policy data is same as descriptor");
                    } else {
                        logger.debug("Policy data changed, updating");
                        policy.update(policyDescriptor);
                    }
                } catch (NotFoundException e) {
                    logger.debug("Policy not found, creating: " + policyDescriptor);
                    api.createPolicy(policyDescriptor);
                }
            }
            ClientApplication clientApplication = null;
            try {
                clientApplication = environment.getOrganization().findClientApplicationByName(clientAppName);
                logger.debug("Client application found: " + clientAppName);
            } catch (NotFoundException e) {
                //
            }
            if (clientApplication == null && isCreateClientApplication()) {
                logger.debug("Client application not found, creating: " + clientAppName);
                clientApplication = environment.getOrganization().createClientApplication(clientAppName, clientApp.getUrl(), clientApp.getDescription());
            }
            result.setClientApplication(clientApplication);
            if (slaTiers != null) {
                for (SLATierDescriptor slaTierDescriptor : slaTiers) {
                    try {
                        SLATier slaTier = api.findSLATier(slaTierDescriptor.getName());
                    } catch (NotFoundException e) {
                        api.createSLATier(slaTierDescriptor.getName(), slaTierDescriptor.getDescription(), slaTierDescriptor.isAutoApprove(), slaTierDescriptor.getLimits());
                    }
                }
            }
            if (access != null) {
                if (clientApplication == null) {
                    throw new InvalidStateException("Client Application doesn't exist and automatic client application creation (createClientApplication) set to false");
                }
                for (APIAccessDescriptor accessDescriptor : access) {
                    AssetInstance instance = environment.getOrganization().getClient().findOrganizationById(accessDescriptor.getGroupId())
                            .findExchangeAsset(accessDescriptor.getGroupId(), accessDescriptor.getAssetId()).findInstances(accessDescriptor.getLabel());
                    Environment apiEnv = environment.getClient().findOrganizationById(instance.getOrganizationId()).findEnvironmentById(instance.getEnvironmentId());
                    API accessedAPI = apiEnv.findAPIByExchangeAsset(accessDescriptor.getGroupId(), accessDescriptor.getAssetId(),
                            accessDescriptor.getAssetVersion(), accessDescriptor.getLabel());
                    APIContract contract;
                    try {
                        contract = accessedAPI.findContract(clientApplication);
                    } catch (NotFoundException e) {
                        SLATier slaTier = accessDescriptor.getSlaTier() != null ? accessedAPI.findSLATier(accessDescriptor.getSlaTier()) : null;
                        contract = clientApplication.requestAPIAccess(accessedAPI, slaTier);
                    }
                    if (!contract.isApproved() && config.isAutoApproveAPIAccessRequest()) {
                        if (contract.isRevoked()) {
                            contract.restoreAccess();
                        } else {
                            contract.approveAccess();
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new ProvisioningException(e);
        }

//                // Create client application
//                ClientApplication clientApplication;
//                String clientAppUrl = this.getClientAppUrl();
//                String clientAppDescription = this.getClientAppDescription();
//                if (clientAppDescription == null) {
//                    clientAppDescription = this.getDescription();
//                }
//                try {
//                    logger.debug("Searching for existing client application {}", clientAppName);
//                    clientApplication = org.findClientApplicationByName(clientAppName);
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
    public synchronized List<APIAccessDescriptor> getAccess() {
        return access != null ? access : Collections.emptyList();
    }

    public synchronized void setAccess(List<APIAccessDescriptor> access) {
        this.access = access;
    }

    public synchronized APIProvisioningDescriptor addAccess(APIAccessDescriptor accessDescriptor) {
        if (access == null) {
            access = new ArrayList<>();
        }
        access.add(accessDescriptor);
        return this;
    }

    public synchronized APIProvisioningDescriptor addAccess(API api) {
        addAccess(new APIAccessDescriptor(api, null));
        return this;
    }

    public synchronized APIProvisioningDescriptor addAccess(API api, String slaTier) {
        addAccess(new APIAccessDescriptor(api, slaTier));
        return this;
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

    public void addPolicy(PolicyDescriptor policy) {
        getPolicies().add(policy);
    }

    @JsonProperty
    public synchronized List<PolicyDescriptor> getPolicies() {
        if (policies == null) {
            policies = new ArrayList<>();
        }
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

    public synchronized List<SLATierDescriptor> getSlaTiers() {
        return slaTiers;
    }

    public synchronized void setSlaTiers(List<SLATierDescriptor> slaTiers) {
        this.slaTiers = slaTiers;
    }

    public synchronized APIProvisioningDescriptor addSlaTier(String name, String description, boolean autoApprove, SLATierLimits... limits) {
        return addSlaTier(new SLATierDescriptor(name, description, autoApprove, limits));
    }

    public synchronized APIProvisioningDescriptor addSlaTier(String name, boolean autoApprove, SLATierLimits... limits) {
        return addSlaTier(name, null, autoApprove, limits);
    }

    public synchronized APIProvisioningDescriptor addSlaTier(SLATierDescriptor slaTierDescriptor) {
        if (slaTiers == null) {
            slaTiers = new ArrayList<>();
        }
        slaTiers.add(slaTierDescriptor);
        return this;
    }
}
