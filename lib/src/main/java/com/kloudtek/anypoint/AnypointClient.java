package com.kloudtek.anypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.provision.*;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("SameParameterValue")
public class AnypointClient implements Closeable, Externalizable {
    public static final String LOGIN_PATH = "/accounts/login";
    protected JsonHelper jsonHelper;
    protected HttpHelper httpHelper;
    private int maxParallelDeployments;
    private ExecutorService deploymentThreadPool;
    private String username;
    private String password;
    private ProvisioningService provisioningService;

    /**
     * Contructor used for serialization only
     **/
    public AnypointClient() {
        jsonHelper = new JsonHelper(this);
        ServiceLoader<ProvisioningService> provisioningServiceLoader = ServiceLoader.load(ProvisioningService.class);
        Iterator<ProvisioningService> provisioningServiceIterator = provisioningServiceLoader.iterator();
        if (provisioningServiceIterator.hasNext()) {
            provisioningService = provisioningServiceIterator.next();
            if (provisioningServiceIterator.hasNext()) {
                throw new IllegalStateException("Found multiple implementations of ProvisioningService");
            }
        } else {
            provisioningService = new ProvisioningServiceImpl();
        }
    }

    public AnypointClient(String username, String password) {
        this(username, password, 3);
    }

    public AnypointClient(String username, String password, int maxParallelDeployments) {
        this();
        this.username = username;
        this.password = password;
        this.maxParallelDeployments = maxParallelDeployments;
        httpHelper = new HttpHelper(this, username, password);
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

    public TransformList provision(Organization parent, File file, ProvisioningConfig provisioningConfig, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException {
        return provisioningService.provision(this, parent, file, provisioningConfig, envSuffix);
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

    public Organization getOrganization(String id) throws HttpException, NotFoundException {
        Organization organization = new Organization(this, id);
        try {
            String json = httpHelper.httpGet(organization.getUriPath());
            jsonHelper.readJson(organization, json);
            return organization;
        } catch (HttpException e) {
            if (e.getStatusCode() == 404) {
                throw new NotFoundException("Enable to find organization " + id, e);
            } else {
                throw e;
            }
        }
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
        return user.getOrganization().createSubOrganization(name, user.getId(), true, true);
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

    public String authenticate(String username, String password) throws HttpException {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        Map data = jsonHelper.toJsonMap(httpHelper.httpPost(LOGIN_PATH, request));
        return data.get("token_type") + " " + data.get("access_token");
    }

    public Environment findEnvironment(String organizationName, String environmentName, boolean createOrganization, boolean createEnvironment, Environment.Type createEnvironmentType) throws NotFoundException, HttpException {
        Organization organization;
        try {
            organization = findOrganization(organizationName);
        } catch (NotFoundException e) {
            if (createOrganization) {
                organization = createOrganization(organizationName);
            } else {
                throw e;
            }
        }
        try {
            return organization.findEnvironment(environmentName);
        } catch (NotFoundException e) {
            if (createEnvironment) {
                return organization.createEnvironment(environmentName, createEnvironmentType);
            } else {
                throw e;
            }
        }
    }

    public static String parseEL(String str, Map<String, String> provisioningParams) {
        if (str == null) {
            return null;
        }
        StringWriter result = new StringWriter();
        StringWriter v = null;
        State state = State.NORMAL;
        for (char c : str.toCharArray()) {
            switch (state) {
                case NORMAL:
                    if (c == '$') {
                        state = State.STARTPARSE;
                        v = new StringWriter();
                    } else {
                        result.append(c);
                    }
                    break;
                case STARTPARSE:
                    if (c == '{') {
                        state = State.PARSE;
                    } else {
                        if (c != '$') {
                            result.append('$');
                        }
                        result.append(c);
                        state = State.NORMAL;
                    }
                    break;
                case PARSE:
                    if (c == '}') {
                        result.append(processElExp(v.toString(), provisioningParams));
                        v = null;
                        state = State.NORMAL;
                    } else {
                        v.append(c);
                    }
            }
        }
        return result.toString();
    }

    private static String processElExp(String exp, Map<String, String> provisioningParams) {
        String val = provisioningParams.get(exp);
        return val != null ? val : "";
    }

    public enum State {
        NORMAL, STARTPARSE, PARSE
    }
}
