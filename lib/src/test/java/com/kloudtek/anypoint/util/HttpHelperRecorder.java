package com.kloudtek.anypoint.util;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.UnexpectedException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpHelperRecorder extends HttpHelper {
    private HttpHelperRecording recording = new HttpHelperRecording();

    public HttpHelperRecorder(AnypointClient client, String username, String password, String orgName) {
        super(client, username, password);
        recording.setOrgName(orgName);
    }

    public HttpHelperRecorder(CloseableHttpClient httpClient, AnypointClient client, String username, String password, String orgName) {
        super(httpClient, client, username, password);
        recording.setOrgName(orgName);
    }

    public HttpHelperRecording getRecording() {
        return recording;
    }

    @Override
    protected String executeWrapper(@NotNull HttpRequestBase method, MultiPartRequest multiPartRequest) throws HttpException {
        HttpHelperOperation op = new HttpHelperOperation(method.getMethod(),method.getURI().toString());
        recording.addOperation(op);
        if( method instanceof HttpEntityEnclosingRequestBase ) {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase) method).getEntity();
            if(entity != null && entity.isRepeatable() ) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                try {
                    entity.writeTo(buf);
                } catch (IOException e) {
                    throw new UnexpectedException(e);
                }
                op.setContent(StringUtils.base64Encode(buf.toByteArray()));
            }
        }
        String json = super.executeWrapper(method, multiPartRequest);
        op.setResult(json);
        return json;
    }
}
