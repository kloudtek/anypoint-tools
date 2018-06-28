package com.kloudtek.anypoint;

import com.kloudtek.anypoint.api.DesignCenterProject;
import com.kloudtek.anypoint.api.DesignCenterProjectExchange;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import org.junit.jupiter.api.Test;

public class APIProvisioningTests extends AbstractAnypointTest {
    public static final String TESTAPI1 = "testapi1";
    public static final String TESTAPI2 = "testapi2";

    @Test
    public void testProvisioning() throws Exception {
        createAPIAsset(TESTAPI1,true);
        createAPIAsset(TESTAPI2,true);
        APIProvisioningConfig config = new APIProvisioningConfig();
        config.setVariable("url", "http://foo");
        new APIProvisioningDescriptor(TESTAPI1,"1.0.0").provision(env, config);
        new APIProvisioningDescriptor(TESTAPI2,"1.0.0").provision(env, config);
    }

    private DesignCenterProject createAPIAsset(String name, boolean publish) throws HttpException {
        DesignCenterProject apiProject;
        try {
            apiProject = org.findDesignCenterProject(name);
        } catch (NotFoundException e) {
            System.out.println("Creating API "+ name);
            apiProject = org.createDesignCenterProject(name,"raml",false,client.getUser().getId());
        }
        if( publish ) {
            DesignCenterProjectExchange exchange = apiProject.getExchange("master");
            if( ! exchange.isPublishedVersion() ) {
                exchange.publish();
            }
        }
        return apiProject;
    }
}
