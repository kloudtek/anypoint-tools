package com.kloudtek.anypoint;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.util.HttpHelper;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class HttpHelperTests {
    public static final String MYUSERNAME = "myusername";
    public static final String MYPASSWORD = "mypassword";
    private int callCount = 0;

    @Test
    public void testRetries() throws Exception {
        CloseableHttpClient httpClient = Mockito.mock(CloseableHttpClient.class);
        AnypointClient anypointClient = Mockito.mock(AnypointClient.class);
        Mockito.when(anypointClient.authenticate(MYUSERNAME, MYPASSWORD)).thenReturn("authtoken");
        HttpHelper httpHelper = new HttpHelper(httpClient, anypointClient, MYUSERNAME, MYPASSWORD);
        httpHelper.setMaxRetries(1);
        httpHelper.setRetryDelay(0L);
        Mockito.when(httpClient.execute(any(HttpRequestBase.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                callCount++;
                CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
                if (callCount == 1) {
                    Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("http", 2, 2), 500, "BOOM ANYPOINT EXPLODED"));
                } else {
                    Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("http", 2, 2), 200, "It's ok now"));
                }
                return response;
            }
        });
        httpHelper.httpGet("/foo");
        assertEquals(2, callCount);
    }
}