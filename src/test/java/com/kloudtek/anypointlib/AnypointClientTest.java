package com.kloudtek.anypointlib;

import com.kloudtek.anypointlib.api.*;
import com.kloudtek.anypointlib.api.policy.ClientIdEnforcementPolicy;
import com.kloudtek.anypointlib.api.policy.Policy;
import com.kloudtek.anypointlib.runtime.Application;
import com.kloudtek.anypointlib.runtime.ApplicationDeploymentFailedException;
import com.kloudtek.anypointlib.runtime.Server;
import com.kloudtek.anypointlib.runtime.ServerGroup;
import com.kloudtek.anypointlib.util.ClassPathStreamSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class AnypointClientTest {
    private static final Logger logger = Logger.getLogger(AnypointClientTest.class.getName());
    public static final String SERVER_GROUP = "testServerGroupDeleteme";
    public static final String ENV_NAME = "testenvdeletemenow";
    public static final String ORG_NAME = "testorgdeletemeplease";
    public static final String API_NAME = "testapideletemeapi";
    public static final String API_NAME2 = "testapideletemeapi2";
    public static final String V1 = "v1";
    public static final String API_ENDPOINT = "http://somewhere.com";
    public static final String API_DESC = "DELETE ME IF YOU SEE ME";
    public static final String PORTAL_NAME = "testportal";
    public static final String CLIENTAPPNAME = "test-delete-me-now";
    public static final String DEPLOYAPP = "testdeployapplication";
    private static String deployOrg = System.getProperty("org");
    private static String deployEnv = System.getProperty("env");
    private static String deployServer = System.getProperty("server");
    private static AnypointClient client = new AnypointClient(System.getProperty("username"), System.getProperty("password"));
    private static User user;
    private static Organization org;

    @BeforeAll
    public static void init() throws HttpException {
        user = client.getUser();
        try {
            org = client.findOrganization(ORG_NAME);
            logger.info("Found org " + ORG_NAME + " deleting");
            org.delete();
            logger.info("Deleted " + ORG_NAME);
        } catch (NotFoundException e) {
            // Ok
        }
        org = user.getOrganization().createSubOrganization(ORG_NAME, user.getId(), true, true);
        try {
            ClientApplication app = org.findClientApplication(CLIENTAPPNAME);
            logger.info("Found test app, deleting");
            app.delete();
        } catch (NotFoundException e) {
            // ok
        }
    }

    @AfterAll
    public static void cleanup() {
        logger.info("Cleaning up");
        try {
            for (Environment environment : org.getEnvironments()) {
                if (environment.getName().contains("deleteme")) {
                    try {
                        environment.delete();
                    } catch (HttpException e) {
                        logger.log(Level.WARNING, "Failed to delete environment: " + environment.getName() + " - " + environment.getId(), e);
                    }
                }
            }
            org.delete();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while cleaning up", e);
        }
    }

    @Test
    public void testRuntimeManagement() throws Exception {
        Organization org = client.findOrganization(ORG_NAME);
        Environment cenv = org.createEnvironment(ENV_NAME, Environment.Type.SANDBOX);
        assertNotNull(cenv.getOrganization());
        String envName = cenv.getName();
        assertTrue(envName.startsWith(ENV_NAME));
        Environment env = org.findEnvironment(envName);
        assertNotNull(env.getOrganization());
        assertEquals(env.getId(), cenv.getId());
        String key = env.getServerRegistrationKey();
        System.out.println("Reg Key=" + key);
        ServerGroup serverGroup = env.createServerGroup(SERVER_GROUP);
        assertNotNull(serverGroup.getParent());
        assertNotNull(serverGroup.getId());
        Server serverGroup2 = env.findServer(SERVER_GROUP);
        assertNotNull(serverGroup2.getParent());
        assertNotNull(serverGroup2.getId());
        assertTrue(serverGroup2 instanceof ServerGroup);
        assertEquals(serverGroup.getId(), serverGroup2.getId());
        env.rename(envName + "2");
        env.delete();
    }

    @Test
    public void testAPIManagement() throws Exception {
        assertNotNull(org.createAPI(API_NAME, V1, API_ENDPOINT, API_DESC));
        assertNotNull(org.createAPI(API_NAME2, V1, API_ENDPOINT, API_DESC));
        APIList list = org.getAPIs(null, 0, 1);
        assertEquals(2, list.getTotal());
        assertEquals(1, list.getLoadedSize());
        HashSet<String> expected = new HashSet<>(Arrays.asList(API_NAME, API_NAME2));
        for (API a : list) {
            assertTrue(expected.remove(a.getName()));
        }
        assertTrue(expected.isEmpty(), "expected apis not empty: " + expected);
        API api = org.getAPI(API_NAME);
        assertNotNull(api);
        APIVersion version = api.getVersion(V1);
        assertNotNull(version);
        List<APIPortal> portals = api.getPortals();
        assertTrue(portals.isEmpty());
        APIVersion apiVersion = api.getVersion(V1);
        APIPortal portal = apiVersion.createPortal(PORTAL_NAME);
        assertEquals(PORTAL_NAME, portal.getName());
        portals = api.getPortals();
        assertEquals(1, portals.size());
        portal = portals.iterator().next();
        assertEquals(PORTAL_NAME, portal.getName());
        apiVersion = apiVersion.updatePortalId(portal.getId());
        assertEquals(portal.getId(), apiVersion.getPortalId());
        assertEquals(0, apiVersion.getPolicies(false).size());
        apiVersion.createPolicy(ClientIdEnforcementPolicy.createBasicAuthClientIdEnforcementPolicy(apiVersion));
        List<Policy> policies = apiVersion.getPolicies(true);
        assertEquals(1, policies.size());
        ClientApplication clientApplication = org.createClientApplication(CLIENTAPPNAME, "http://testclientapp", "test client app");
        assertNotNull(clientApplication);
        assertNotNull(clientApplication.getId());
        APIAccessContract contract = clientApplication.requestAPIAccess(apiVersion);
        assertNotNull(contract);
        assertNotNull(contract.getId());
        assertEquals("APPROVED", contract.getStatus());
    }

    @Test
    public void testDeployment() throws NotFoundException, IOException, HttpException, ApplicationDeploymentFailedException {
        Server server = client.findOrganization(deployOrg).findEnvironment(deployEnv).findServer(deployServer);
        Application app = server.deploy(DEPLOYAPP, new ClassPathStreamSource(this.getClass(), "/deletemeapp-1.0.0-SNAPSHOT.zip"));
        app.waitDeployed();
    }
}