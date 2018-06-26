package com.kloudtek.anypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.api.DesignCenterProject;
import com.kloudtek.anypoint.api.DesignCenterProjectExchange;
import com.kloudtek.anypoint.util.AnypointClientMock;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.anypoint.util.HttpHelperOperation;
import com.kloudtek.anypoint.util.HttpHelperRecorder;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
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
    private List<HttpHelperOperation> httpOperations = new ArrayList<>();
    protected DesignCenterProject apiProject;

    @BeforeEach
    public void init(TestInfo testInfo) throws Exception {
        String username = System.getProperty("username");
        String password = System.getProperty("password");
        integrationTest = StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password);
        if (integrationTest) {
            String testName = getTestName(testInfo);
            System.out.println("Recording test: "+testName);
            File resourceDir = new File("src/test/resources");
            if( ! resourceDir.exists() ) {
                throw new RuntimeException("Resource directory not found (make sure the test is using project root as working dir): "+ resourceDir.getPath());
            }
            testRecordingFile = new File(resourceDir, "test-" + getTestName(testInfo) + ".json");
            client = new AnypointClient(username, password);
            try {
                org = client.findOrganization(AP_TEST);
            } catch (NotFoundException e) {
                System.out.println("Creating test org "+AP_TEST);
                org = client.createOrganization(AP_TEST);
            }
            try {
                env = org.findEnvironment(AP_TEST);
            } catch (NotFoundException e) {
                System.out.println("Creating test env "+AP_TEST);
                env = org.createEnvironment(AP_TEST, Environment.Type.SANDBOX);
            }
            try {
                apiProject = org.findDesignCenterProject(AP_TEST);
            } catch (NotFoundException e) {
                System.out.println("Creating API "+AP_TEST);
                apiProject = org.createDesignCenterProject(AP_TEST,"raml",false,client.getUser().getId());
            }
            DesignCenterProjectExchange exchange = apiProject.getExchange("master");
            if( ! exchange.isPublishedVersion() ) {
                exchange.publish();
            }
            client.setHttpHelper(new HttpHelperRecorder(client,username,password));
        } else {
            client = new AnypointClientMock();
            org = client.getOrganization(AnypointClientMock.ORGID);
            env = org.findEnvironment("DEV");
        }
    }

    @AfterEach
    public void cleanup() throws IOException, HttpException {
        if( testRecordingFile != null ) {
            new ObjectMapper().writer().writeValue(testRecordingFile,httpOperations);
        }
        if( org.getName().equals(AP_TEST) ) {
            System.out.println("Deleting test org "+AP_TEST);
            org.delete();
        }
    }

    public static String getTestName(TestInfo testInfo) {
        return testInfo.getDisplayName().replaceAll("\\(\\)","");
    }
}
