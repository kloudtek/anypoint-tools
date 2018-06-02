package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class APIManagementTests extends AbstractAnypointTest {
    public static final String ENDPOINT_URL = "http://someurl";
    public static final String FOOLABEL = "foolabel";
    public static final String PVER = "v1";

//    @Test
//    public void testCreateAPI() throws HttpException {
//        APISpecList specs = org.findAPISpecs("Test");
//        assertEquals(1,specs.size());
//        APISpec apiSpec = specs.getApiDefinitions().get(0);
//        assertEquals("Test",apiSpec.getName());
//        assertEquals("v1",apiSpec.getProductAPIVersion());
//        verifyCreatedAPI(env.createAPI(apiSpec, true, ENDPOINT_URL, FOOLABEL));
//        APIList apiList = env.findAPIs(null);
//        assertEquals(1,apiList.getTotal());
//        assertEquals(1,apiList.size());
//        APIAsset apiAsset = apiList.getAssets().get(0);
//        assertNotNull(apiAsset.getApis());
//        assertEquals(1,apiAsset.getApis().size());
//        verifyCreatedAPI(apiAsset.getApis().get(0));
//    }
//
//    private void verifyCreatedAPI(API api) {
//        assertEquals(PVER,api.getProductVersion());
//        assertEquals(FOOLABEL,api.getInstanceLabel());
//        assertEquals(env.getId(),api.getEnvironmentId());
//    }
}
