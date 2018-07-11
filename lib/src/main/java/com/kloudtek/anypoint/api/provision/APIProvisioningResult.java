package com.kloudtek.anypoint.api.provision;

import com.kloudtek.anypoint.api.API;
import com.kloudtek.anypoint.api.ClientApplication;

public class APIProvisioningResult {
    private API api;
    private ClientApplication clientApplication;

    public API getApi() {
        return api;
    }

    public void setApi(API api) {
        this.api = api;
    }

    public ClientApplication getClientApplication() {
        return clientApplication;
    }

    public void setClientApplication(ClientApplication clientApplication) {
        this.clientApplication = clientApplication;
    }
}
