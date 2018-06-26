package com.kloudtek.anypoint.util;

import com.kloudtek.anypoint.AnypointClient;
import org.apache.http.impl.client.CloseableHttpClient;

public class HttpHelperRecorder extends HttpHelper {
    public HttpHelperRecorder(AnypointClient client, String username, String password) {
        super(client, username, password);
    }

    public HttpHelperRecorder(CloseableHttpClient httpClient, AnypointClient client, String username, String password) {
        super(httpClient, client, username, password);
    }
}
