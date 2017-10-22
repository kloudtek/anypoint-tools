package com.kloudtek.anypointlib;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public class AnypointClient implements Closeable {
    static final String LOGIN_PATH = "/accounts/login";
    private JsonHelper jsonHelper;
    private HttpHelper httpHelper;

    public AnypointClient(String username, String password) {
        jsonHelper = new JsonHelper(this);
        httpHelper = new HttpHelper(this, username, password);
    }

    @Override
    public void close() throws IOException {
        httpHelper.close();
    }

    public List<Organization> getOrganizations() throws HttpException {
        String json = httpHelper.httpGet("/accounts/api/me");
        ArrayList<Organization> list = new ArrayList<>();
        for (JsonNode node: jsonHelper.readJsonTree(json).at("/user/memberOfOrganizations")) {
            list.add(jsonHelper.readJson(new Organization(this),node));
        }
        return list;
    }

    @NotNull
    public Organization getOrganizationByName(String name) throws NotFoundException, HttpException {
        for (Organization organization : getOrganizations()) {
            if (organization.getName().equals(name)) {
                return organization;
            }
        }
        throw new NotFoundException("Organization not found: " + name);
    }

    /**
     * Return details on the account used to administer anypoint
     *
     * @return User details
     */
    public User getUser() throws HttpException {
        String json = httpHelper.httpGet("/accounts/api/me");
        return jsonHelper.readJson(new User(), json, "/user");
    }

    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }

    public HttpHelper getHttpHelper() {
        return httpHelper;
    }

    void authenticate(String username, String password) throws HttpException {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        Map data = jsonHelper.toJsonMap(httpHelper.httpPost(LOGIN_PATH, request));
        httpHelper.setAuth(data.get("token_type") + " " + data.get("access_token"));
    }
}
