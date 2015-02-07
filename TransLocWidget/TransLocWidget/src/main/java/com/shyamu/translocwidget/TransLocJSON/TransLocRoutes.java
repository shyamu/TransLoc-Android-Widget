package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TransLocRoutes {

    @JsonProperty("rate_limit")
    private int rateLimit;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("api_latest_version")
    private String apiLatestVersion;
    @JsonProperty("generated_on")
    private String generatedOn;
    @JsonProperty("data")
    private List<TransLocRoute> data;
    @JsonProperty("api_version")
    private String apiVersion;

    public List<TransLocRoute> getData() {
        return data;
    }


}
