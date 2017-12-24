package com.kloudtek.anypoint.api.policy;

import com.kloudtek.anypoint.api.APIVersion;
import com.kloudtek.anypoint.util.JsonHelper;

import java.util.Map;

public class ClientIdEnforcementPolicy extends Policy {
    private String credentialsOrigin;
    private String clientIdExpression;
    private String clientSecretExpression;

    public ClientIdEnforcementPolicy(APIVersion parent, String clientIdExpression, String clientSecretExpression) {
        super(parent);
        credentialsOrigin = "customExpression";
        this.clientIdExpression = clientIdExpression;
        this.clientSecretExpression = clientSecretExpression;
    }

    ClientIdEnforcementPolicy(APIVersion parent, Map<String, Object> data) {
        super(parent, data);
        Map<String, String> cfgData = (Map<String, String>) data.get("configurationData");
        if (cfgData != null) {
            credentialsOrigin = cfgData.get("credentialsOrigin");
            clientIdExpression = cfgData.get("clientIdExpression");
            clientSecretExpression = cfgData.get("clientSecretExpression");
        }
    }

    @Override
    public JsonHelper.MapBuilder toJson() {
        JsonHelper.MapBuilder json = super.toJson().set("policyTemplateId", "client-id-enforcement");
        json.addMap("configurationData").set("credentialsOrigin", credentialsOrigin)
                .set("clientIdExpression", clientIdExpression).set("clientSecretExpression", clientSecretExpression);
        return json;
    }

    public String getCredentialsOrigin() {
        return credentialsOrigin;
    }

    public void setCredentialsOrigin(String credentialsOrigin) {
        this.credentialsOrigin = credentialsOrigin;
    }

    public String getClientIdExpression() {
        return clientIdExpression;
    }

    public void setClientIdExpression(String clientIdExpression) {
        this.clientIdExpression = clientIdExpression;
    }

    public String getClientSecretExpression() {
        return clientSecretExpression;
    }

    public void setClientSecretExpression(String clientSecretExpression) {
        this.clientSecretExpression = clientSecretExpression;
    }

    public static ClientIdEnforcementPolicy createBasicAuthClientIdEnforcementPolicy(APIVersion parent) {
        ClientIdEnforcementPolicy policy = new ClientIdEnforcementPolicy(parent, "#[authorization = message.inboundProperties['authorization']; if (authorization == null) return ''; base64token = authorization.substring(6); token = new String(org.apache.commons.codec.binary.Base64.decodeBase64(base64token.getBytes())); delim = token.indexOf(':'); clientId = delim == -1 ? '' : token.substring(0, delim); return clientId ]", "#[authorization = message.inboundProperties['authorization']; if (authorization == null) return ''; base64token = authorization.substring(6); token = new String(org.apache.commons.codec.binary.Base64.decodeBase64(base64token.getBytes())); delim = token.indexOf(':'); clientSecret = delim == -1 ? '' : token.substring(delim+1); return clientSecret ]");
        policy.credentialsOrigin = "httpBasicAuthenticationHeader";
        return policy;
    }
}
