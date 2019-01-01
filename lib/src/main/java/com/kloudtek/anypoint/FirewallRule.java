package com.kloudtek.anypoint;

public class FirewallRule {
    private String cidrBlock;
    private String protocol;
    private String fromPort;
    private String toPort;

    public FirewallRule(String cidrBlock, String protocol, String fromPort, String toPort) {
        this.cidrBlock = cidrBlock;
        this.protocol = protocol;
        this.fromPort = fromPort;
        this.toPort = toPort;
    }

    public FirewallRule() {
    }

    public String getCidrBlock() {
        return cidrBlock;
    }

    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public String getToPort() {
        return toPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }
}
