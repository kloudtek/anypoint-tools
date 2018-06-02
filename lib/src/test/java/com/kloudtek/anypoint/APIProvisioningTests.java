package com.kloudtek.anypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.api.provision.APIProvisioningConfig;
import com.kloudtek.anypoint.api.provision.APIProvisioningDescriptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class APIProvisioningTests extends AbstractAnypointTest {
    @Test
    public void testProvisioning() throws Exception {
        APIProvisioningDescriptor apiProvisioningDescriptor = new ObjectMapper().readValue(getClass().getResourceAsStream("/api-provisioning.json"), APIProvisioningDescriptor.class);
        APIProvisioningConfig config = new APIProvisioningConfig();
        config.setVariable("url","http://foo");
        apiProvisioningDescriptor.provision(env, config);
    }
}
