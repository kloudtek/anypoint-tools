package com.kloudtek.anypointlib;

import com.kloudtek.util.ThreadUtils;
import com.kloudtek.util.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

public class HttpHelper implements Closeable {
    private static final String HEADER_AUTH = "Authorization";
    private final CloseableHttpClient httpClient;
    private String auth;
    private AnypointClient client;
    private final String username;
    private final String password;

    public HttpHelper(AnypointClient client, String username, String password) {
        this.client = client;
        this.username = username;
        this.password = password;
        httpClient = HttpClients.createMinimal();
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    public String httpGet(String path, Environment env) throws HttpException {
        return execute(new HttpGet(convertPath(path)), env);
    }

    public String httpGet(String path) throws HttpException {
        return execute(new HttpGet(convertPath(path)));
    }

    public String httpPost(String path, Object data, Environment env) throws HttpException {
        return execute(new HttpPost(convertPath(path)), data, env);
    }

    public String httpPost(String path, Object data) throws HttpException {
        return execute(new HttpPost(convertPath(path)), data);
    }

    public String httpPatch(String path, Object data) throws HttpException {
        return execute(new HttpPatch(convertPath(path)), data);
    }

    public String httpPut(String path, Object data) throws HttpException {
        return execute(new HttpPut(convertPath(path)), data);
    }

    public String httpDelete(String path) throws HttpException {
        return execute(new HttpDelete(convertPath(path)));
    }

    public String httpDelete(@NotNull String path, @NotNull Environment env) throws HttpException {
        return execute(new HttpDelete(convertPath(path)), env);
    }

    private String execute(@NotNull HttpEntityEnclosingRequestBase method, @NotNull Object data) throws HttpException {
        if (data instanceof HttpEntity) {
            method.setEntity((HttpEntity) data);
        } else {
            method.setHeader("Content-Type", "application/json");
            method.setEntity(new ByteArrayEntity(client.getJsonHelper().toJson(data)));
        }
        return execute(method);
    }

    private String execute(@NotNull HttpEntityEnclosingRequestBase method, @NotNull Object data, @NotNull Environment env) throws HttpException {
        env.addHeaders(method);
        return execute(method, data);
    }

    private String execute(@NotNull HttpRequestBase method, @NotNull Environment env) throws HttpException {
        env.addHeaders(method);
        return execute(method);
    }

    private String execute(@NotNull HttpRequestBase method) throws HttpException {
        if (auth == null && !method.getURI().getPath().equals(AnypointClient.LOGIN_PATH)) {
            client.authenticate(username, password);
        }
        try {
            return doExecute(method);
        } catch (HttpException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 401) {
                client.authenticate(username, password);
                return doExecute(method);
            } else if (e.getStatusCode() == 500) {
                ThreadUtils.sleep(1500);
                return doExecute(method);
            } else {
                throw e;
            }
        }
    }

    @Nullable
    private String doExecute(HttpRequestBase method) throws HttpException {
        if (auth != null && method.getFirstHeader(HEADER_AUTH) == null) {
            method.setHeader(HEADER_AUTH, auth);
        }
        try (CloseableHttpResponse response = httpClient.execute(method)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode > 299) {
                String errMsg;
                if (response.getEntity() != null && response.getEntity().getContent() != null) {
                    errMsg = " : " + IOUtils.toString(response.getEntity().getContent());
                } else {
                    errMsg = "";
                }
                throw new HttpException("Anypoint returned status code " + statusCode + " - url: " + method.getURI() + " - err: " + errMsg, statusCode);
            }
            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                return IOUtils.toString(response.getEntity().getContent());
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), e);
        }
    }

    private String convertPath(String path) {
        return path.startsWith("/") ? "https://anypoint.mulesoft.com" + path : path;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
