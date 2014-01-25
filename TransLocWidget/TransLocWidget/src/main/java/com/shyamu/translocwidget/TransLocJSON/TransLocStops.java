package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class TransLocStops{

    @JsonProperty("rate_limit")
    public int rateLimit;

    @JsonProperty("expires_in")
    public int expiresIn;

    @JsonProperty("api_latest_version")
    public double apiLatestVersion;

    @JsonProperty("generated_on")
    public Date generatedOn;

    @JsonProperty("data")
    public List<TransLocStop> data;

    @JsonProperty("api_version")
    public Double apiVersion;
}