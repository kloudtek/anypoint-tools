package com.kloudtek.anypoint.provisioning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.FirewallRule;

import java.util.List;

public class VPCProvisioningDescriptor {
    private String name;
    private String cidrBlock;
    private boolean defaultVpc;
    private String region;
    private List<VPCOrgProvisioningDescriptor> organizations;
    private List<String> environments;
    private List<FirewallRule> firewallRules;
    private List<String> dnsServers;
    private List<String> dnsDomains;

    public VPCProvisioningDescriptor() {
    }

    @JsonProperty(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(required = true)
    public String getCidrBlock() {
        return cidrBlock;
    }

    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    @JsonProperty(required = true)
    public boolean isDefaultVpc() {
        return defaultVpc;
    }

    public void setDefaultVpc(boolean defaultVpc) {
        this.defaultVpc = defaultVpc;
    }

    @JsonProperty(required = true)
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @JsonProperty(required = true)
    public List<VPCOrgProvisioningDescriptor> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<VPCOrgProvisioningDescriptor> organizations) {
        this.organizations = organizations;
    }

    @JsonProperty(required = true)
    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }

    @JsonProperty(required = true)
    public List<FirewallRule> getFirewallRules() {
        return firewallRules;
    }

    public void setFirewallRules(List<FirewallRule> firewallRules) {
        this.firewallRules = firewallRules;
    }

    @JsonProperty(required = true)
    public List<String> getDnsServers() {
        return dnsServers;
    }

    public void setDnsServers(List<String> dnsServers) {
        this.dnsServers = dnsServers;
    }

    @JsonProperty(required = true)
    public List<String> getDnsDomains() {
        return dnsDomains;
    }

    public void setDnsDomains(List<String> dnsDomains) {
        this.dnsDomains = dnsDomains;
    }
}
