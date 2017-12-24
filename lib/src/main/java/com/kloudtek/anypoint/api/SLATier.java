package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointClient;
import com.kloudtek.anypoint.AnypointObject;

import java.util.ArrayList;
import java.util.List;

public class SLATier extends AnypointObject<APIVersion> {
    private Integer id;
    private String name;

    public SLATier(AnypointClient client) {
        super(client);
    }

    public SLATier(APIVersion parent) {
        super(parent);
    }

    public SLATier() {
    }

    public static List<String> getNames(List<SLATier> slaTier) {
        ArrayList<String> names = new ArrayList<>(slaTier.size());
        for (SLATier tier : slaTier) {
            if (tier.getName() != null) {
                names.add(tier.getName());
            }
        }
        return names;
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
