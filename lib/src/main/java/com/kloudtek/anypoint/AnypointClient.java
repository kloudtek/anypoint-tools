package com.kloudtek.anypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.kloudtek.anypoint.deploy.DeploymentService;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.JsonHelper;
import com.kloudtek.util.UnexpectedException;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("SameParameterValue")
public class AnypointClient implements Closeable, Serializable {
    public static final String LOGIN_PATH = "/accounts/login";
    protected JsonHelper jsonHelper;
    protected HttpHelper httpHelper;
    private int maxParallelDeployments = 5;
    private transient ExecutorService deploymentThreadPool;
    private String username;
    private String password;
    private DeploymentService deploymentService;

    /**
     * Contructor used for serialization only
     **/
    public AnypointClient() {
        init();
    }

    private void init() {
        jsonHelper = new JsonHelper(this);
        deploymentService = loadService(DeploymentService.class);
        deploymentThreadPool = Executors.newFixedThreadPool(maxParallelDeployments);
    }

    private <X> X loadService(Class<X> serviceClass) {
        X service;
        ServiceLoader<X> serviceLoader = ServiceLoader.load(serviceClass);
        Iterator<X> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            service = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException("Found multiple implementations of ProvisioningService");
            }
        } else {
            try {
                if( serviceClass.isInterface() ) {
                    service = Class.forName(serviceClass.getName()+"Impl").asSubclass(serviceClass).newInstance();
                } else {
                    service = serviceClass.newInstance();
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new UnexpectedException(e);
            }
        }
        if( service instanceof Service ) {
            ((Service) service).setClient(this);
        }
        return service;
    }

    public AnypointClient(String username, String password) {
        this(username, password, 3);
    }

    public AnypointClient(String username, String password, int maxParallelDeployments) {
        this.username = username;
        this.password = password;
        this.maxParallelDeployments = maxParallelDeployments;
        httpHelper = new HttpHelper(this, username, password);
        init();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        deploymentThreadPool = Executors.newFixedThreadPool(maxParallelDeployments);
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
        if (deploymentThreadPool != null) {
            deploymentThreadPool.shutdown();
        }
    }

    public List<Organization> findOrganizations() throws HttpException {
        String json = httpHelper.httpGet("/accounts/api/me");
        ArrayList<Organization> list = new ArrayList<>();
        for (JsonNode node : jsonHelper.readJsonTree(json).at("/user/memberOfOrganizations")) {
            list.add(jsonHelper.readJson(createOrganizationObject(), node));
        }
        return list;
    }

    @NotNull
    public Organization findOrganization(String name) throws NotFoundException, HttpException {
        for (Organization organization : findOrganizations()) {
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

    public void setHttpHelper(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
        httpHelper.setClient(this);
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

    public String getUserId() throws HttpException {
        // TODO cache this
        return getUser().getId();
    }

    public DeploymentService getDeploymentService() {
        return deploymentService;
    }

    public void applyAlerts(File alertsJsonFile) throws IOException, HttpException {
        throw new RuntimeException("Not implemented");
    }

    @NotNull
    protected Organization createOrganizationObject() {
        return new Organization(this);
    }
}
