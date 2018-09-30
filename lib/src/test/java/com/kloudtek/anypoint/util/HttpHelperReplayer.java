package com.kloudtek.anypoint.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.anypoint.HttpException;
import org.apache.http.client.methods.HttpRequestBase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHelperReplayer extends HttpHelper {
    private String orgName;
    private Map<String, List<HttpHelperOperation>> opMap = new HashMap<>();

    public HttpHelperReplayer(@NotNull File recordingFile) throws IOException {
        HttpHelperRecording recording = new ObjectMapper().readValue(recordingFile, HttpHelperRecording.class);
        orgName = recording.getOrgName();
        for (HttpHelperOperation op : recording.getOperations()) {
            String idx = op.getMethod() + "-" + op.getPath();
            List<HttpHelperOperation> ol = opMap.computeIfAbsent(idx, k -> new ArrayList<>());
            ol.add(op);
        }
    }

    @Override
    protected String executeWrapper(@NotNull HttpRequestBase method, MultiPartRequest multiPartRequest) throws HttpException {
        List<HttpHelperOperation> opList = opMap.get(method.getMethod() + "-" + method.getURI().toString());
        if (opList != null && !opList.isEmpty()) {
            if (opList.size() > 1) {
                return opList.remove(0).getResult();
            } else {
                return opList.get(0).getResult();
            }
        } else {
            throw new HttpException("no recorded operation for " + method.getMethod() + "-" + method.getURI().toString());
        }
    }

    public String getOrgName() {
        return orgName;
    }
}
