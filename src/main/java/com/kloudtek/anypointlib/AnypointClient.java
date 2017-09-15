package com.kloudtek.anypointlib;

import com.kloudtek.util.httpclient.HttpClient;
import com.kloudtek.util.httpclient.HttpClientFactory;

public class AnypointClient {
    private HttpClient httpClient;

    public AnypointClient() {
        this(HttpClientFactory.create());
    }

    public AnypointClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void authenticate(String username, String password) {
        String authReq = "{\"username\":\"${username}\",\"password\":\"${password}\"}";
        httpClient.postJson("https://anypoint.mulesoft.com/accounts/login",authReq);
    }

    public static void main(String[] args) {
        AnypointClient client = new AnypointClient();
        client.authenticate("ymenagerpetco","rvV9p/oNWj8RAzAs");
    }
}
