package com.kloudtek.anypoint.provisioning;

import java.util.List;

public class VPCOrgProvisioningDescriptor {
    private String name;
    private List<String> environments;

    public VPCOrgProvisioningDescriptor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }
}
