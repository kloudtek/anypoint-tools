package com.kloudtek.anypoint.provision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.APIVersion;
import com.kloudtek.anypoint.api.policy.Policy;
import com.kloudtek.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

public class ProvisioningDescriptor {
    private ProvisioningServiceImpl provisioningService;
    private ZipFile zipFile;
    private Map<String, String> provisioningParams;
    private String envSuffix;
    private List<ProvisionedAPI> apis;

    public ProvisioningDescriptor(ProvisioningServiceImpl provisioningService, ZipFile zipFile, Map<String, String> provisioningParams,
                                  String envSuffix) {
        this.provisioningService = provisioningService;
        this.zipFile = zipFile;
        this.provisioningParams = provisioningParams;
        this.envSuffix = envSuffix;
    }

    @JsonProperty
    public List<ProvisionedAPI> getApis() {
        return apis != null ? apis : Collections.emptyList();
    }

    public void setApis(List<ProvisionedAPI> apis) {
        this.apis = apis;
    }

    public void provision(ZipFile zipFile, Organization org) throws NotFoundException, HttpException, IOException {
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
                        version = api.getVersion(apiVersionName);
                    } catch (NotFoundException e) {
                        version = api.createVersion(apiVersionName, endpoint, description);
                    }
                } catch (NotFoundException e) {
                    api = org.createAPI(apiName, apiVersionName, endpoint, description);
                    version = api.getVersion(apiVersionName);
                }
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
                        version.updatePolicy(policyDescriptor.toPolicy(version));
                    }
                }
                for (ProvisionedAPIAccess access : provisionedAPI.getAccess()) {
                    org.requestAPIAccess(clientAppName, parseEL(access.getName()), parseEL(access.getVersion()), true, true, null);
                }
            }
        } catch (ClassCastException e) {
            throw new IOException("Invalid anypoint.json descriptor", e);
        }
    }

    private String parseEL(String str) {
        return provisioningService.parseEL(str, provisioningParams);
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
}
