package com.kloudtek.anypoint.util;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.util.ThreadUtils;
import com.kloudtek.util.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);
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
        logger.debug("HTTP GET " + path + " env=" + env);
        return execute(new HttpGet(convertPath(path)), env);
    }

    public String httpGet(String path) throws HttpException {
        logger.debug("HTTP GET " + path);
        return executeWrapper(new HttpGet(convertPath(path)), null);
    }

    public String httpPost(String path, Object data, Environment env) throws HttpException {
        logger.debug("HTTP POST " + path + " env=" + env + " data=" + data);
        return execute(new HttpPost(convertPath(path)), data, env);
    }

    public String httpPost(String path, Object data) throws HttpException {
        logger.debug("HTTP POST " + path + " data=" + data);
        return execute(new HttpPost(convertPath(path)), data);
    }

    public String httpPatch(String path, Object data) throws HttpException {
        logger.debug("HTTP PATCH " + path + " data=" + data);
        return execute(new HttpPatch(convertPath(path)), data);
    }

    public String httpPut(String path, Object data) throws HttpException {
        logger.debug("HTTP PUT " + path + " data=" + data);
        return execute(new HttpPut(convertPath(path)), data);
    }

    public String httpDelete(String path) throws HttpException {
        logger.debug("HTTP DELETE " + path);
        return executeWrapper(new HttpDelete(convertPath(path)), null);
    }

    public String httpDelete(@NotNull String path, @NotNull Environment env) throws HttpException {
        logger.debug("HTTP DELETE " + path + " env=" + env);
        return execute(new HttpDelete(convertPath(path)), env);
    }

    public MultiPartRequest createMultiPartPostRequest(String url, Environment environment) {
        HttpPost request = new HttpPost(convertPath(url));
        environment.addHeaders(request);
        return new MultiPartRequest(request);
    }

    public MultiPartRequest createMultiPartPatchRequest(String url, Environment environment) {
        HttpPatch request = new HttpPatch(convertPath(url));
        environment.addHeaders(request);
        return new MultiPartRequest(request);
    }

    private String execute(@NotNull HttpEntityEnclosingRequestBase method, @NotNull Object data) throws HttpException {
        if (data instanceof HttpEntity) {
            method.setEntity((HttpEntity) data);
        } else {
            method.setHeader("Content-Type", "application/json");
            method.setEntity(new ByteArrayEntity(client.getJsonHelper().toJson(data)));
        }
        return executeWrapper(method, null);
    }

    private String execute(@NotNull HttpEntityEnclosingRequestBase method, @NotNull Object data, @NotNull Environment env) throws HttpException {
        env.addHeaders(method);
        return execute(method, data);
    }

    private String execute(@NotNull HttpRequestBase method, @NotNull Environment env) throws HttpException {
        env.addHeaders(method);
        return executeWrapper(method, null);
    }

    private String executeWrapper(@NotNull HttpRequestBase method, MultiPartRequest multiPartRequest) throws HttpException {
        boolean authenticating = method.getURI().getPath().equals(AnypointClient.LOGIN_PATH);
        if (auth == null && !authenticating) {
            client.authenticate(username, password);
        }
        try {
            if (multiPartRequest != null) {
                ((HttpEntityEnclosingRequestBase) method).setEntity(multiPartRequest.toEntity());
            }
            return doExecute(method);
        } catch (HttpException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 401 && !authenticating) {
                client.authenticate(username, password);
                return doExecute(method);
            } else if (e.getStatusCode() > 500) {
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
            throw new RuntimeIOException(e);
        }
    }

    private String convertPath(String path) {
        return path.startsWith("/") ? "https://anypoint.mulesoft.com" + path : path;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public class MultiPartRequest {
        private Map<String, Object> parts = new HashMap<>();
        private HttpEntityEnclosingRequestBase request;

        MultiPartRequest(HttpEntityEnclosingRequestBase request) {
            this.request = request;
        }

        public MultiPartRequest addText(@NotNull String name, @NotNull String value) {
            parts.put(name, value);
            return this;
        }

        public MultiPartRequest addBinary(@NotNull String name, @NotNull StreamSource streamSource) {
            parts.put(name, streamSource);
            return this;
        }

        HttpEntity toEntity() throws HttpException {
            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                for (Map.Entry<String, Object> e : parts.entrySet()) {
                    if (e.getValue() instanceof String) {
                        builder.addTextBody(e.getKey(), (String) e.getValue());
                    } else if (e.getValue() instanceof StreamSource) {
                        builder.addBinaryBody(e.getKey(), ((StreamSource) e.getValue()).createInputStream(),
                                ContentType.APPLICATION_OCTET_STREAM, ((StreamSource) e.getValue()).getFileName());
                    }
                }
                return builder.build();
            } catch (IOException e) {
                throw new HttpException("Failed to read data to send: " + e.getMessage(), e);
            }
        }

        public String execute() throws HttpException, IOException {
            try {
                logger.debug("HTTP {}", request);
                return HttpHelper.this.executeWrapper(request, this);
            } catch (RuntimeIOException e) {
                throw e.getIOException();
            }
        }
    }

    public class RuntimeIOException extends RuntimeException {
        @NotNull
        private IOException e;

        RuntimeIOException(@NotNull IOException ioException) {
            this.e = ioException;
        }

        @NotNull
        IOException getIOException() {
            return e;
        }
    }
}
