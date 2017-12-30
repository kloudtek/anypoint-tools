package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.Organization;
import com.kloudtek.anypoint.util.PaginatedList;
import com.kloudtek.util.URLBuilder;

import java.util.List;

public class ClientApplicationList extends PaginatedList<ClientApplication, Organization> {
    public ClientApplicationList(Organization organization, String filter) {
        super(organization);
        // TODO ignoring filter because @#$#@$@# anypoint filter is broken
    }

    @Override
    protected URLBuilder buildUrl() {
        return new URLBuilder(parent.getUriPath() + "/applications");
    }

    @JsonProperty
    public List<ClientApplication> getApplications() {
        return list;
    }

    public void setApplications(List<ClientApplication> applications) {
        this.list = applications;
    }
}
