package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.Environment;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.util.PaginatedList;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APIList extends PaginatedList<APIAsset, Environment> {
    private final String filter;

    public APIList(Environment environment, String filter) throws HttpException {
        this(environment, filter, 20);
    }

    public APIList(Environment environment, String filter, int limit) throws HttpException {
        super(environment, limit);
        this.filter = filter;
        download();
    }

    @Override
    protected @NotNull URLBuilder buildUrl() {
        URLBuilder urlBuilder = new URLBuilder("/apimanager/api/v1/organizations/" + parent.getParent().getId() + "/environments/" + parent.getId() + "/apis")
                .param("ascending", "true");
        if (filter != null) {
            urlBuilder.param("query", filter);
        }
        urlBuilder.param("sort", "createdDate");
        return urlBuilder;
    }

    @JsonProperty
    public List<APIAsset> getAssets() {
        return list;
    }

    public void setAssets(List<APIAsset> assets) {
        list = assets;
    }
}
