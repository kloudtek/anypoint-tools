package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.SLATierLimits;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import com.kloudtek.anypoint.api.provision.APIProvisioningResult;
import com.kloudtek.anypoint.api.provision.SLATierDescriptor;
import com.kloudtek.anypoint.util.AbstractAnypointTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APIProvisioningITCase extends AbstractAnypointTest {
    @Test
    public void testProvisioning() throws Exception {
        integrationTest = true;
        createAPIAsset(TESTAPI1, true);
        createAPIAsset(TESTAPI2, true);
        APIProvisioningConfig config = new APIProvisioningConfig();
        config.setVariable("url", "http://foo");
        // provision api 1
        APIProvisioningDescriptor apd1 = addClientIdPolicy(new APIProvisioningDescriptor(TESTAPI1, V1));
        APIProvisioningResult res1 = provision(config, apd1);
        // provision api 2
        APIProvisioningDescriptor apd2 = addClientIdPolicy(new APIProvisioningDescriptor(TESTAPI2, V1));
        apd2.addAccess(res1.getApi());
        apd2.addSlaTier(new SLATierDescriptor("testtier", false, new SLATierLimits(true, 1, 1)));
        APIProvisioningResult res2 = provision(config, apd2);
        // test changing client id expression
        assertEquals(2, env.findAPIs(null).size());
        checkPolicy(TESTAPI2, V1, ATTRIBUTES_HEADERS_CLIENT_SECRET);
        apd2.getPolicies().get(0).getData().put(CLIENT_SECRET_EXPRESSION, ATTRIBUTES_HEADERS_CLIENT_SECRET2);
        provision(config, apd2);
        checkPolicy(TESTAPI2, V1, ATTRIBUTES_HEADERS_CLIENT_SECRET2);
        // request access to api2 from api1
        apd1.addAccess(res2.getApi());
        provision(config, apd1);
//        res2.getApi().refresh();
    }
}
