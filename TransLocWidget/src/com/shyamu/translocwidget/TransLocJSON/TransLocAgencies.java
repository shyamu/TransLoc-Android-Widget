package com.shyamu.translocwidget.TransLocJSON;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransLocAgencies{

    @JsonProperty("rate_limit")
    public int rateLimit;

    @JsonProperty("expires_in")
    public int expiresIn;

    @JsonProperty("api_latest_version")
    public double apiLatestVersion;

    @JsonProperty("generated_on")
    public Date generatedOn;

    @JsonProperty("data")private List<TransLocAgency> data;

    @JsonProperty("api_version")
    public Double apiVersion;
public List<TransLocAgency> getData() {
        return data;
    }public void setData(List<TransLocAgency> data) {
    this.data = data;
}}