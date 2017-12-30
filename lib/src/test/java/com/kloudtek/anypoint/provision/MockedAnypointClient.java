package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.util.HttpHelper;
import com.kloudtek.util.URLBuilder;
import com.kloudtek.util.io.IOUtils;
import org.mockito.Mockito;

public class MockedAnypointClient extends AnypointClient {
    public static final String ORGID = "orgid";
    public static final String API_ID = "90485399";
    public static final String API_VERSION_ID = "6894188";
    public static final String API_NAME = "deleteme";
    public static final String API_NAME_TESTAPP = "testapp";
    public static final String CLIENT_APP_ID = "154284";

    public MockedAnypointClient() {
        httpHelper = Mockito.mock(HttpHelper.class);
    }

    public void mockHttpGet(String path, String contentPath) {
        try {
            String content = IOUtils.toString(getClass().getResourceAsStream("/" + contentPath));
            Mockito.when(httpHelper.httpGet(path)).thenReturn(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mockGetAPIs(String orgId, String query, String contentPath) {
        URLBuilder urlBuilder = new URLBuilder("/apiplatform/repository/v2/organizations/" + orgId + "/apis?ascending=false&limit=25&offset=0&sort=createdDate");
        if (query != null) {
            urlBuilder.param("query", query);
        }
        mockHttpGet(urlBuilder.toString(), contentPath);
    }

    public Organization setupDefaultMocks() {
        Organization org = new Organization(this, ORGID);
        mockGetAPIs(ORGID, API_NAME, "api-list.json");
        mockGetAPIVersion(ORGID, API_ID, API_VERSION_ID, "get-api-version.json");
        mockGetPolicies(ORGID, API_ID, API_VERSION_ID, false, "get-policies.json");
        mockGetAPIs(ORGID, API_NAME_TESTAPP, "api-list-query-testapp.json");
        mockListApplications(ORGID, "list-applications.json");
        mockListContracts(ORGID, CLIENT_APP_ID, "list-contracts.json");
        mockGetClientApplication(ORGID, CLIENT_APP_ID, "get-clientapp.json");
        return org;
    }

    public void mockGetAPIVersion(String orgId, String apiId, String apiVersionId, String contentPath) {
        mockHttpGet("/apiplatform/repository/v2/organizations/" + orgId + "/apis/" + apiId + "/versions/" + apiVersionId, contentPath);
    }

    public void mockGetPolicies(String orgId, String apiId, String apiVersionId, boolean fullInfo, String contentPath) {
        mockHttpGet("/apiplatform/repository/v2/organizations/" + orgId + "/apis/" + apiId + "/versions/" + apiVersionId + "/policies?fullInfo=" + fullInfo, contentPath);
    }

    public void mockListApplications(String orgId, String contentPath) {
        mockHttpGet("/apiplatform/repository/v2/organizations/" + orgId + "/applications?limit=100&offset=0&ascending=true", contentPath);
    }

    public void mockListContracts(String orgId, String appId, String contentPath) {
        mockHttpGet("/apiplatform/repository/v2/organizations/" + orgId + "/applications/" + appId + "/contracts", contentPath);
    }

    ///apiplatform/repository/v2/organizations/orgid/applications
    public void mockGetClientApplication(String orgId, String appId, String contentPath) {
        mockHttpGet("/apiplatform/repository/v2/organizations/" + orgId + "/applications/" + appId, contentPath);
    }
}
