package com.kloudtek.anypoint;

public abstract class AbstractService implements Service {
    protected AnypointClient client;

    @Override
    public void setClient(AnypointClient client) {
        this.client = client;
    }
}
