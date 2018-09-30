package com.kloudtek.anypoint.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.anypoint.AnypointObject;
import com.kloudtek.anypoint.HttpException;

import java.util.HashMap;

public class APIContract extends AnypointObject<API> {
    @JsonProperty("requestedTierId")
    private String requestedTierId;
    @JsonProperty("rejectedDate")
    private String rejectedDate;
    @JsonProperty("masterOrganizationId")
    private String masterOrganizationId;
    @JsonProperty("organizationId")
    private String organizationId;
    @JsonProperty("approvedDate")
    private String approvedDate;
    @JsonProperty("tierId")
    private String tierId;
    @JsonProperty("terms")
    private String terms;
    @JsonProperty("revokedDate")
    private String revokedDate;
    @JsonProperty("partyName")
    private String partyName;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("applicationId")
    private Integer applicationId;
    @JsonProperty("partyId")
    private String partyId;
    @JsonProperty("requestedTier")
    private SLATier requestedTier;
    @JsonProperty("apiId")
    private Integer apiId;
    @JsonProperty("status")
    private String status;

    public APIContract(API api) {
        super(api);
    }

    public APIContract() {
    }

    @JsonIgnore
    public boolean isApproved() {
        return status != null && status.equalsIgnoreCase("approved");
    }

    @JsonIgnore
    public boolean isRevoked() {
        return status != null && status.equalsIgnoreCase("revoked");
    }

    @JsonIgnore
    public boolean isPending() {
        return status != null && status.equalsIgnoreCase("pending");
    }

    private String getOpsURLPath() {
        return "/apimanager/xapi/v1/organizations/" + parent.getParent().getParent().getId() + "/environments/" +
                parent.getParent().getId() + "/apis/" + parent.getId() + "/contracts/" + id;
    }

    public String getURLPath() {
        return "/apimanager/api/v1/organizations/" + parent.getParent().getParent().getId() + "/environments/" +
                parent.getParent().getId() + "/apis/" + parent.getId() + "/contracts/" + id;
    }

    public APIContract restoreAccess() throws HttpException {
        String json = httpHelper.httpPost(getOpsURLPath() + "/restore", new HashMap<String, Object>());
        return jsonHelper.readJson(new APIContract(parent), json);
    }

    public APIContract approveAccess() throws HttpException {
        String json = httpHelper.httpPost(getOpsURLPath() + "/approve", new HashMap<String, Object>());
        return jsonHelper.readJson(new APIContract(parent), json);
    }

    public APIContract revokeAccess() throws HttpException {
        String json = httpHelper.httpPost(getOpsURLPath() + "/revoke", new HashMap<String, Object>());
        return jsonHelper.readJson(new APIContract(parent), json);
    }

    public void delete() throws HttpException {
        httpHelper.httpDelete(getURLPath());
    }

    public String getRequestedTierId() {
        return requestedTierId;
    }

    public void setRequestedTierId(String requestedTierId) {
        this.requestedTierId = requestedTierId;
    }

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getMasterOrganizationId() {
        return masterOrganizationId;
    }

    public void setMasterOrganizationId(String masterOrganizationId) {
        this.masterOrganizationId = masterOrganizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getTierId() {
        return tierId;
    }

    public void setTierId(String tierId) {
        this.tierId = tierId;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getRevokedDate() {
        return revokedDate;
    }

    public void setRevokedDate(String revokedDate) {
        this.revokedDate = revokedDate;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public SLATier getRequestedTier() {
        return requestedTier;
    }

    public void setRequestedTier(SLATier requestedTier) {
        this.requestedTier = requestedTier;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
