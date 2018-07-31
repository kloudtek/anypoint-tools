package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.util.PaginatedList;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SLATierList extends PaginatedList<SLATier, API> {
    public SLATierList(API api) throws HttpException {
        super(api);
        download();
    }

    @NotNull
    @Override
    protected URLBuilder buildUrl() {
        return new URLBuilder("/apimanager/api/v1/organizations/" + parent.getParent().getParent().getId() +
                "/environments/" + parent.getParent().getId() + "/apis/" + parent.getId() + "/tiers");
    }

    @JsonProperty
    public List<SLATier> getTiers() {
        return list;
    }

    public void setTiers(List<SLATier> tiers) {
        this.list = tiers;
    }
}
