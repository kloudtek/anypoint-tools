package com.kloudtek.anypointlib.api.policy;

import com.kloudtek.anypointlib.AnypointObject;
import com.kloudtek.anypointlib.JsonHelper;
import com.kloudtek.anypointlib.api.APIVersion;

import java.util.Map;

public abstract class Policy extends AnypointObject<APIVersion> {
    private Integer id;

    public Policy() {
    }

    public Policy(APIVersion parent) {
        super(parent);
    }

    public Policy(APIVersion parent, Map<String,Object> data) {
        super(parent);
        id = (Integer) data.get("id");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JsonHelper.MapBuilder toJson() {
        return jsonHelper.buildJsonMap().set("id",id).set("apiVersionId",parent.getId());
    }

    public static Policy parseJson( APIVersion apiVersion, Map<String,Object> data ) {
        String type = (String) data.get("policyTemplateId");
        if( type.equals("client-id-enforcement") ) {
            return new ClientIdEnforcementPolicy(apiVersion, data);
        } else {
            return new CustomPolicy(apiVersion,data);
        }
    }
}
