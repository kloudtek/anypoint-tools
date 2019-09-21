package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.HttpException;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.util.PaginatedList;
import com.kloudtek.util.URLBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APIContractList extends PaginatedList<APIContract, API> {
    private String orgId;
    private String envId;
    private String apiId;

    public APIContractList(API api) throws HttpException {
        super(api);
        envId = api.getParent().getParent().getId();
        envId = api.getParent().getId();
        apiId = api.getId();
        limit = 50;
        download();
    }

    public APIContractList(API parent, String orgId, String envId, String apiId) throws HttpException {
        super(parent);
        this.orgId = orgId;
        this.envId = envId;
        this.apiId = apiId;
        limit = 50;
        download();
    }

    @NotNull
    @Override
    protected URLBuilder buildUrl() {
        URLBuilder url = new URLBuilder("/apimanager/api/v1/organizations/" + orgId + "/environments/" + envId + "/apis/" + apiId + "/contracts")
                .param("ascending", "true");
        return url;
    }

    @JsonProperty
    public List<APIContract> getContracts() {
        return list;
    }

    public void setContracts(List<APIContract> list) {
        this.list = list;
    }
}
