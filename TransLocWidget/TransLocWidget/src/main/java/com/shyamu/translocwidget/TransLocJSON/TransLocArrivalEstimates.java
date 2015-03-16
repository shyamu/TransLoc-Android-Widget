package com.shyamu.translocwidget.TransLocJSON;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class TransLocArrivalEstimates{

    @SerializedName("rate_limit")
    public int rateLimit;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("api_latest_version")
    public double apiLatestVersion;

    @SerializedName("generated_on")
    public Date generatedOn;

    @SerializedName("data")
    public List<TransLocArrivalEstimate> data;

    @SerializedName("api_version")
    public Double apiVersion;
}