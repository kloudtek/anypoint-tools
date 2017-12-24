package com.kloudtek.anypoint.provision;

import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.NotFoundException;
import com.kloudtek.anypoint.Organization;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public abstract class ProvisioningService {
    public static ProvisioningService getService() {
        return new ProvisioningServiceImpl();
    }

    public abstract void provision(AnypointClient client, Organization parent, String appName, File file, Map<String, String> provisioningParams, String envSuffix) throws IOException, NotFoundException, HttpException, InvalidAnypointDescriptorException;

    public String parseEL(String str, Map<String, String> provisioningParams) {
        if (str == null) {
            return null;
        }
        StringWriter result = new StringWriter();
        StringWriter v = null;
        State state = State.NORMAL;
        for (char c : str.toCharArray()) {
            switch (state) {
                case NORMAL:
                    if (c == '$') {
                        state = State.STARTPARSE;
                        v = new StringWriter();
                    } else {
                        result.append(c);
                    }
                    break;
                case STARTPARSE:
                    if (c == '{') {
                        state = State.PARSE;
                    } else {
                        if (c != '$') {
                            result.append('$');
                        }
                        result.append(c);
                        state = State.NORMAL;
                    }
                    break;
                case PARSE:
                    if (c == '}') {
                        result.append(processElExp(v.toString(), provisioningParams));
                        v = null;
                        state = State.NORMAL;
                    } else {
                        v.append(c);
                    }
            }
        }
        return result.toString();
    }

    private String processElExp(String exp, Map<String, String> provisioningParams) {
        String val = provisioningParams.get(exp);
        return val != null ? val : "";
    }

    public enum State {
        NORMAL, STARTPARSE, PARSE
    }
}
