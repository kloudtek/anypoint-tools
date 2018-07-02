package com.kloudtek.anypoint.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.*;
import com.kloudtek.anypoint.api.DesignCenterProject;
import com.kloudtek.anypoint.api.DesignCenterProjectExchange;
import com.kloudtek.anypoint.exchange.AssetList;
import com.kloudtek.anypoint.exchange.AssetOverview;
import com.kloudtek.anypoint.util.AnypointClientMock;
import com.kloudtek.anypoint.util.HttpHelperOperation;
import com.kloudtek.anypoint.util.HttpHelperRecorder;
import com.kloudtek.anypoint.util.HttpHelperReplayer;
import com.kloudtek.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbstractAnypointTest {
    public static final String AP_TEST = "ATUTestDeleteMe";
    protected AnypointClient client;
    protected @NotNull Organization org;
    protected boolean integrationTest;
    protected Environment env;
    private File testRecordingFile;
    private HttpHelperRecorder httpHelperRecorder;

    @BeforeEach
    public void init(TestInfo testInfo) throws Exception {
        String username = System.getProperty("username");
        String password = System.getProperty("password");
        integrationTest = StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password);
        File resourceDir = new File("src/test/resources");
        if( ! resourceDir.exists() ) {
            throw new RuntimeException("Resource directory not found (make sure the test is using project root as working dir): "+ resourceDir.getPath());
        }
        testRecordingFile = new File(resourceDir, "test-" + getTestName(testInfo) + ".json");
        if (integrationTest) {
            String testName = getTestName(testInfo);
            System.out.println("Recording test: "+testName);
            client = new AnypointClient(username, password);
            try {
                org = client.findOrganization(AP_TEST);
                System.out.println("Test org exists, deleting: "+AP_TEST);
                org.delete();
            } catch (NotFoundException e) {
            }
            System.out.println("Creating test org "+AP_TEST);
            org = client.createOrganization(AP_TEST);
            try {
                env = org.findEnvironment(AP_TEST);
            } catch (NotFoundException e) {
                System.out.println("Creating test env "+AP_TEST);
                env = org.createEnvironment(AP_TEST, Environment.Type.SANDBOX);
            }
            httpHelperRecorder = new HttpHelperRecorder(client, username, password);
            client.setHttpHelper(httpHelperRecorder);
            org = client.findOrganization(AP_TEST);
            env = org.findEnvironment(AP_TEST);
        } else {
            client = new AnypointClient("","");
            client.setHttpHelper(new HttpHelperReplayer(testRecordingFile));
            org = client.findOrganization(AP_TEST);
            env = org.findEnvironment(AP_TEST);
        }
    }

    @AfterEach
    public void cleanup() throws IOException, HttpException {
        if( httpHelperRecorder != null ) {
            new ObjectMapper().writer().writeValue(testRecordingFile,httpHelperRecorder.getOperations());
            if( org.getName().equals(AP_TEST) ) {
                System.out.println("Deleting test org "+AP_TEST);
                org.delete();
            }
        }
    }

    public static String getTestName(TestInfo testInfo) {
        return testInfo.getDisplayName().replaceAll("\\(\\)","");
    }
}
