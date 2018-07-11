package com.kloudtek.anypoint;

import com.kloudtek.anypoint.util.AnypointClientMock;
import com.kloudtek.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class AbstractAnypointTest {
    protected AnypointClient client;
    protected @NotNull Organization org;
    protected boolean integrationTest;
    protected Environment env;

    @BeforeEach
    public void init() throws Exception {
        String username = System.getProperty("username");
        String password = System.getProperty("password");
        integrationTest = StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password);
        if (integrationTest) {
            client = new AnypointClient(username, password);
            org = client.findOrganization("Sandbox");
            env = org.findEnvironment("Sandbox");
        } else {
            client = new AnypointClientMock();
            org = client.getOrganization(AnypointClientMock.ORGID);
            env = org.findEnvironment("DEV");
        }
    }

    @AfterEach
    public void cleanup() throws HttpException {
//        if( integrationTest ) {
//            org.delete();
//        }
    }
}
