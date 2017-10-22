package com.kloudtek.anypointlib.runtime;

import com.kloudtek.anypointlib.Environment;
import com.kloudtek.anypointlib.HttpException;

import java.io.IOException;

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
}
