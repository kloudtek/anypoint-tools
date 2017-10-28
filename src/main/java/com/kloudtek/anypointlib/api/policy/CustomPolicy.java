package com.kloudtek.anypointlib.api.policy;

import com.kloudtek.anypointlib.api.APIVersion;
import com.kloudtek.anypointlib.util.JsonHelper;

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
