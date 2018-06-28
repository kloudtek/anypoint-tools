package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.util.PaginatedList;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APISpecList extends PaginatedList<APISpec, Organization> {
    private final String filter;

    public APISpecList(Organization organization, String filter) throws HttpException {
        super(organization);
        this.filter = filter;
        limit = 50;
        download();
    }

    @NotNull
    @Override
    protected URLBuilder buildUrl() {
        URLBuilder url = new URLBuilder("/apimanager/xapi/v1/organizations/" + parent.getId() + "/apiSpecs")
                .param("ascending", "true");
        if (filter != null) {
            url.param("searchTerm", filter);
        }
        return url;
    }

    @JsonProperty
    public List<APISpec> getApiDefinitions() {
        return list;
    }

    public void setApiDefinitions(List<APISpec> list) {
        this.list = list;
    }
}
