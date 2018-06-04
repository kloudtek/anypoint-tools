package com.kloudtek.anypoint.runtime;

import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;

import java.util.Map;

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
        httpHelper.httpDelete("/hybrid/api/v1/serverGroups/" + id, parent);
    }

    public void addServer(Server server) throws HttpException {
        addServer(server.getId());
    }

    public void addServer(String serverId) throws HttpException {
        Map<String, Object> request = jsonHelper.buildJsonMap().set("serverGroupId", id).set("serverId", serverId).toMap();
        httpHelper.httpPost("/hybrid/api/v1/serverGroups/" + id + "/servers/" + serverId, request, parent);
    }
}
