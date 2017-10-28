package com.kloudtek.anypoint.api.policy;

import com.kloudtek.anypoint.api.APIVersion;
import com.kloudtek.anypoint.util.JsonHelper;

import java.util.Map;

public class CustomPolicy extends Policy {
    private Map<String, Object> data;

    public CustomPolicy(APIVersion parent, Map<String, Object> data) {
        super(parent, data);
        this.data = data;
    }

    @Override
    public JsonHelper.MapBuilder toJson() {
        return jsonHelper.buildJsonMap(data);
    }
}
