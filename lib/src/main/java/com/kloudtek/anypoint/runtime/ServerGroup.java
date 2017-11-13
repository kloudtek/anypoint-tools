package com.kloudtek.anypoint.runtime;

import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;

public class ServerGroup extends Server {
    public ServerGroup() {
    }

    public ServerGroup(Environment environment) {
        super(environment);
    }

    public ServerGroup(Environment environment, String id) {
        super(environment, id);
    }

    @Override
    public void delete() throws HttpException {
        httpHelper.httpDelete("https://anypoint.mulesoft.com/hybrid/api/v1/serverGroups/"+id,parent);
    }

    public void addServer(Server server) {

    }
}
