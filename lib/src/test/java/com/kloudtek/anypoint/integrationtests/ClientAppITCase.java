package com.kloudtek.anypoint.integrationtests;

import com.kloudtek.anypoint.*;
import com.kloudtek.anypoint.api.*;
import com.kloudtek.util.DataUtils;
import com.kloudtek.util.UnexpectedException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientAppITCase {
    private static final Logger logger = LoggerFactory.getLogger(ClientAppITCase.class);
    private static Organization organization;
    private static Environment env;
    private static AnypointClient client;
    private static ClientApplication clientApp;
    private static String clientAppName;
    private static API api;
    private static APIContract contract;

    static {
        try {
            clientAppName = "int-test-" + DataUtils.uuidToB32Str(UUID.randomUUID());
            client = new AnypointClient();
            organization = client.findOrganization("mySubOrg");
            clientApp = null;
            env = organization.findEnvironmentByName("Sandbox");
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

//    @AfterAll
    public static void cleanup() {
        if( contract != null ) {
            try {
                contract.delete();
            } catch (HttpException e) {
                logger.warn("Failed to delete contract: " + contract.getId());
            }
        }
        if (clientApp != null) {
            try {
                clientApp.delete();
            } catch (Exception e) {
                logger.warn("Failed to delete client app: " + clientAppName);
            }
        }
    }

//    @Order(1)
//    @Test
    public void testCreateClientApplication() throws HttpException, NotFoundException {
        clientApp = organization.createClientApplication(clientAppName, null, "test client app");
    }

//    @Test
//    @Order(2)
    public void testCreateContract() throws HttpException, NotFoundException {
        if (clientApp == null) {
            throw new IllegalStateException("Client App not created");
        }
        APIList list = client.findOrganization("mySubOrg").findEnvironmentByName("Sandbox").findAPIs("someotherorgapi");
        assertEquals(1,list.getTotal());
        APIAsset apiAsset = list.iterator().next();
        List<API> apis = apiAsset.getApis();
        api = apis.iterator().next();
        contract = api.requestAPIAccess(clientApp);
    }
}
