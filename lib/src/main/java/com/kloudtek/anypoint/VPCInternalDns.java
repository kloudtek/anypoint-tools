package com.kloudtek.anypoint;

import java.util.List;

public class VPCInternalDns {
    private List<String> dnsServers;
    private List<String> specialDomains;

    public VPCInternalDns() {
    }

    public VPCInternalDns(List<String> dnsServers, List<String> specialDomains) {
        this.dnsServers = dnsServers;
        this.specialDomains = specialDomains;
    }

    public List<String> getDnsServers() {
        return dnsServers;
    }

    public void setDnsServers(List<String> dnsServers) {
        this.dnsServers = dnsServers;
    }

    public List<String> getSpecialDomains() {
        return specialDomains;
    }

    public void setSpecialDomains(List<String> specialDomains) {
        this.specialDomains = specialDomains;
    }
}
