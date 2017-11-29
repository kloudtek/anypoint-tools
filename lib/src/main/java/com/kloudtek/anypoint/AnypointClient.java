package com.kloudtek.anypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("SameParameterValue")
public class AnypointClient implements Closeable, Externalizable {
    public static final String LOGIN_PATH = "/accounts/login";
    private JsonHelper jsonHelper;
    private HttpHelper httpHelper;
    private int maxParallelDeployments;
    private ExecutorService deploymentThreadPool;
    private String username;
    private String password;

    /**
     * Contructor used for serialization only
     **/
    public AnypointClient() {
    }

    public AnypointClient(String username, String password) {
        this(username, password, 3);
    }

    public AnypointClient(String username, String password, int maxParallelDeployments) {
        jsonHelper = new JsonHelper(this);
        httpHelper = new HttpHelper(this, username, password);
        this.username = username;
        this.password = password;
        this.maxParallelDeployments = maxParallelDeployments;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(username);
        out.writeUTF(password);
        out.writeInt(maxParallelDeployments);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        username = in.readUTF();
        password = in.readUTF();
        maxParallelDeployments = in.readInt();
        jsonHelper = new JsonHelper(this);
        httpHelper = new HttpHelper(this, username, password);
    }

    public int getMaxParallelDeployments() {
        return maxParallelDeployments;
    }

    public synchronized void setMaxParallelDeployments(int maxParallelDeployments) {
        if (maxParallelDeployments <= 0) {
            throw new IllegalArgumentException("maxParallelDeployments " + maxParallelDeployments + " is invalid (must be greater than 0)");
        }
        this.maxParallelDeployments = maxParallelDeployments;
        if (deploymentThreadPool != null) {
            deploymentThreadPool.shutdown();
        }
        deploymentThreadPool = Executors.newFixedThreadPool(maxParallelDeployments);
    }

    @Override
    public void close() throws IOException {
        httpHelper.close();
    }

    public List<Organization> getOrganizations() throws HttpException {
        String json = httpHelper.httpGet("/accounts/api/me");
        ArrayList<Organization> list = new ArrayList<>();
        for (JsonNode node : jsonHelper.readJsonTree(json).at("/user/memberOfOrganizations")) {
            list.add(jsonHelper.readJson(new Organization(this), node));
        }
        return list;
    }

    @NotNull
    public Organization findOrganization(String name) throws NotFoundException, HttpException {
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

    public Organization createOrganization(String name) throws HttpException {
        User user = getUser();
        return user.getOrganization().createSubOrganization(name, user.getId(), false, false);
    }

    public Organization createOrganization(String name, String ownerId, boolean createSubOrgs, boolean createEnvironments) throws HttpException {
        return getUser().getOrganization().createSubOrganization(name, ownerId, createSubOrgs, createEnvironments);
    }

    public Organization createOrganization(String name, String ownerId, boolean createSubOrgs, boolean createEnvironments,
                                           boolean globalDeployment, int vCoresProduction, int vCoresSandbox, int vCoresDesign,
                                           int staticIps, int vpcs, int loadBalancer) throws HttpException {
        return getUser().getOrganization().createSubOrganization(name, ownerId, createSubOrgs,
                createEnvironments, globalDeployment, vCoresProduction, vCoresSandbox, vCoresDesign, staticIps, vpcs, loadBalancer);
    }

    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }

    public HttpHelper getHttpHelper() {
        return httpHelper;
    }

    public void authenticate(String username, String password) throws HttpException {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        Map data = jsonHelper.toJsonMap(httpHelper.httpPost(LOGIN_PATH, request));
        httpHelper.setAuth(data.get("token_type") + " " + data.get("access_token"));
    }
}
