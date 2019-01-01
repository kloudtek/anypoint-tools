package com.kloudtek.anypoint;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class VPC extends AnypointObject<Organization> {
    private String id;
    private String name;
    private String cidrBlock;
    @JsonProperty("isDefault")
    private boolean defaultVpc;
    private String region;
    private List<String> sharedWith;
    private List<String> associatedEnvironments;
    private List<FirewallRule> firewallRules;
    private VPCInternalDns internalDns;

    public VPC() {
    }

    public VPC(String name) {
        this.name = name;
    }

    public VPC(String name, String cidrBlock, boolean defaultVpc, String region) {
        this.name = name;
        this.cidrBlock = cidrBlock;
        this.defaultVpc = defaultVpc;
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCidrBlock() {
        return cidrBlock;
    }

    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    public boolean isDefaultVpc() {
        return defaultVpc;
    }

    public void setDefaultVpc(boolean defaultVpc) {
        this.defaultVpc = defaultVpc;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public List<String> getAssociatedEnvironments() {
        return associatedEnvironments;
    }

    public void setAssociatedEnvironments(List<String> associatedEnvironments) {
        this.associatedEnvironments = associatedEnvironments;
    }

    public List<FirewallRule> getFirewallRules() {
        return firewallRules;
    }

    public void setFirewallRules(List<FirewallRule> firewallRules) {
        this.firewallRules = firewallRules;
    }

    public VPCInternalDns getInternalDns() {
        return internalDns;
    }

    public void setInternalDns(VPCInternalDns internalDns) {
        this.internalDns = internalDns;
    }

    public void delete() throws HttpException {
        if( id == null ) {
            throw new IllegalArgumentException("VPC id missing");
        }
        client.getHttpHelper().httpDelete("/cloudhub/api/organizations/" + parent.getId() + "/vpcs/" + id);
    }
}
